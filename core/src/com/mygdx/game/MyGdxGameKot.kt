package com.mygdx.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import kotlin.math.roundToInt

class MyGdxGameKot : ApplicationAdapter() {
    lateinit var stage: Stage
    lateinit var batch: SpriteBatch
    lateinit var img: Texture
    // Tile unit is calculated in order that
    // we are able to fit exactly 10 tiles on x axis on each phone
    var TILE_UNIT: Float? = null
    // How much is height of the display bigger than its width
    var aspectRatio: Int? = null


    val chunkList = mutableListOf<Chunk>()

    lateinit var viewport: Viewport
    lateinit var camera: OrthographicCamera


    override fun create() {
        createChunks()
        aspectRatio = Gdx.graphics.height / Gdx.graphics.width
        TILE_UNIT = Gdx.graphics.width / 10f
        viewport = ScreenViewport(OrthographicCamera(TILE_UNIT!!, aspectRatio!! * TILE_UNIT!!))
        stage = Stage(viewport)
        camera = viewport.camera as OrthographicCamera
        Gdx.input.inputProcessor = getInputProcessor()
        batch = SpriteBatch()
        img = Texture("map_tile_sand.png")
    }

    override fun render() {

        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Note that camera position x and y is in the center of camera
        val cameraBottomLeftX = (camera.position.x - (camera.viewportWidth / 2)).roundToInt()
        val cameraBottomLeftY = (camera.position.y - (camera.viewportHeight / 2)).roundToInt()

        val cameraTopRightX = (camera.position.x + (camera.viewportWidth / 2)).roundToInt()
        val cameraTopRightY = (camera.position.y + (camera.viewportHeight / 2)).roundToInt()
        val cameraBottomLeft = Point(cameraBottomLeftX, cameraBottomLeftY)
        val cameraTopRight = Point(cameraTopRightX, cameraTopRightY)

        val chunksToDisplay = chunkList.filter {
            val isOverlaping = it.isOverlaping(cameraBottomLeft, cameraTopRight)
            isOverlaping
        }

        batch.projectionMatrix = camera.combined
        batch.begin()

        chunksToDisplay.forEach { chunk ->
            chunk.tiles.forEachIndexed { index, tile ->
                batch.draw(img, (tile.xUnits * TILE_UNIT!!) + chunk.bottomLeft.x, (tile.yUnits * TILE_UNIT!!)  + chunk.bottomLeft.y)
            }
        }
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        img.dispose()
    }

    private fun getInputProcessor(): InputAdapter {
        var lastX = 0
        var lastY = 0

        return object : InputAdapter() {
            override fun scrolled(amount: Int): Boolean {
                Gdx.app.log("LibGDX", "Scrolled by: $amount")
                return super.scrolled(amount)
            }

            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                val moveYBy = (lastY - screenY).toFloat()
                val moveXBy = (lastX - screenX).toFloat()

                lastY = screenY
                lastX = screenX

              //  Gdx.app.log("LibGDX", "Moving camera by x: " + moveXBy + "and y: " + moveYBy)
                camera.translate(moveXBy, -moveYBy)
                camera.update()
            //   Gdx.app.log("LibGDX", "Dragged by: x: $screenX and y: $screenY")
                return true
            }

            override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
                lastX = x
                lastY = y
                Gdx.app.log("LibGDX", "touchDown: x$x and y $y")
                return true
            }

            override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
                return true
            }
        }
    }

    private fun createChunks() {
        val xTilesPerChunk = numberOfTiles / chunkCountHor
        val yTilesPerChunk = numberOfTiles / chunkCountVer

        val chunkSum = chunkCountHor * chunkCountVer


        val chunkTiles = mutableListOf<Tile>()
        for (chunkOrder in 0 until chunkSum) {

            for (x in 0 until xTilesPerChunk) {
                for (y in 0 until yTilesPerChunk) {
                    chunkTiles.add(Tile(x, y))
                }
            }

            val bottomLeftX = chunkOrder.rem(chunkCountHor) * chunkCountHor
            val bottomLeftY = chunkOrder.div(chunkCountHor) * chunkCountHor
            val uniqueList = mutableListOf<Tile>().apply { addAll(chunkTiles) }
            chunkTiles.clear()
            val chunk = Chunk(Point(bottomLeftX, bottomLeftY), Point(bottomLeftX + chunkCountHor, bottomLeftY + chunkCountVer), uniqueList, chunkOrder)
            chunkList.add(chunk)
        }
    }

    companion object {
        private const val chunkCountHor = 50
        private const val chunkCountVer = 50
        private const val numberOfTiles = 1000
    }
}
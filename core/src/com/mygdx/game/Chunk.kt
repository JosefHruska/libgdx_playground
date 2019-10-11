package com.mygdx.game

data class Tile(val xUnits: Int, val yUnits: Int)

data class Chunk(val bottomLeft: Point, val topRight: Point, val tiles: MutableList<Tile>, val order: Int) {

    fun isOverlaping(otherBottomLeft: Point, otherTopRight: Point): Boolean {
        val xAxisOverlap = (otherTopRight.x > bottomLeft.x) && (otherBottomLeft.x < topRight.x)
        val yAxisOverlap = (otherTopRight.y > bottomLeft.y) && (otherBottomLeft.y < topRight.y)

        return xAxisOverlap && yAxisOverlap
    }
}

data class Point(val x: Int, val y: Int)
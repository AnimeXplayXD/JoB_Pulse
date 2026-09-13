package com.example

import com.example.ui.components.dockIndexForX
import org.junit.Assert.assertEquals
import org.junit.Test

class DockSelectionTest {
    @Test fun crossingBoundary_selectsBeforeRelease() {
        assertEquals(1, dockIndexForX(120f, 400f, 4, 0, 8f))
    }
    @Test fun boundaryJitter_keepsCurrentDestination() {
        assertEquals(0, dockIndexForX(104f, 400f, 4, 0, 8f))
        assertEquals(1, dockIndexForX(96f, 400f, 4, 1, 8f))
    }
    @Test fun outOfBoundsAndMissingLayout_areSafe() {
        assertEquals(3, dockIndexForX(900f, 400f, 4, 0, 8f))
        assertEquals(0, dockIndexForX(-30f, 400f, 4, 2, 8f))
        assertEquals(2, dockIndexForX(100f, 0f, 4, 2, 8f))
    }
}

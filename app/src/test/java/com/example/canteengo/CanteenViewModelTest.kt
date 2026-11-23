package com.example.canteengo

import com.example.canteengo.data.OrderStatus
import com.example.canteengo.ui.CanteenViewModel
import org.junit.Assert.*
import org.junit.Test

class CanteenViewModelTest {
    @Test
    fun cartCombinesItemsCalculatesTotalsAndRemovesZeroQuantity() {
        val model = CanteenViewModel()
        model.add("chicken")
        model.add("chicken")
        model.add("tea")
        assertEquals(15000, model.totalCentavos)
        assertEquals(3, model.itemCount)
        model.changeQuantity("tea", -1)
        assertEquals(13000, model.totalCentavos)
        model.remove("chicken")
        assertTrue(model.cart.isEmpty())
    }

    @Test
    fun ordersSnapshotCartGenerateNumbersAndAdvanceInSequence() {
        val model = CanteenViewModel()
        assertNull(model.placeOrder("Student"))
        model.add("chicken")
        assertNull(model.placeOrder(" "))
        val first = model.placeOrder(" Ana ")!!
        assertTrue(model.cart.isEmpty())
        model.add("tea")
        val second = model.placeOrder("Ben")!!
        assertNotEquals(first, second)
        assertEquals(6500, model.orders.last().totalCentavos)
        assertEquals("Ana", model.orders.last().customer)
        repeat(5) { model.advanceOrder(first) }
        assertEquals(OrderStatus.COMPLETED, model.orders.last().status)
        assertEquals(OrderStatus.PLACED, model.orders.first().status)
    }
}

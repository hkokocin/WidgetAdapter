package de.welt.widgetadapter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.same
import com.nhaarman.mockito_kotlin.then
import com.winterbe.expekt.should
import org.junit.Test

internal class SimpleDiffCallbackTest {

    private val newItem = Any()
    private val oldItem = Any()

    val newItems = listOf(newItem)
    val oldItems = listOf(oldItem)
    val areItemsTheSame: (Any, Any) -> Boolean = mock()
    val areContentsTheSame: (Any, Any) -> Boolean = mock()

    val classToTest = SimpleDiffCallback(newItems, oldItems, areItemsTheSame, areContentsTheSame)

    @Test
    fun canGetOldListSize() {
        classToTest.oldListSize.should.be.equal(1)
    }

    @Test
    fun canGetNewListSize() {
        classToTest.newListSize.should.be.equal(1)
    }

    @Test
    fun useCallbackToDetermineIfItemsAreTheSame() {
        given(areItemsTheSame.invoke(any(), any())).willReturn(true)

        classToTest.areItemsTheSame(0, 0)

        then(areItemsTheSame).should().invoke(same(oldItem)!!, same(newItem)!!)
    }


    @Test
    fun useCallbackToDetermineIfItemsAreEqual() {
        given(areContentsTheSame.invoke(any(), any())).willReturn(true)

        classToTest.areContentsTheSame(0, 0)

        then(areContentsTheSame).should().invoke(same(oldItem)!!, same(newItem)!!)
    }
}


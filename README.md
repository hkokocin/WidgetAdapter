[![Build Status](https://travis-ci.org/hkokocin/WidgetAdapter.svg?branch=master)](https://travis-ci.org/hkokocin/WidgetAdapter)
# WidgetAdapter
This is a small library that eases the management of adapters for ```RecyclerView```s especially if you use different item view types. It is written in Kotlin and intended to be used in Kotlin since it relies on some of its syntactic sugar.
## Install
## Usage
All you need for the WidgetAdapter is a ```LayoutInflater``` and a set of providers that instantiate your ```Widget```s. ```Widget```s are your views that are wrapped by implementations of the ```Widget<T>``` interface.
### Create a WidgetAdapter
```kotlin
val adapter = WidgetAdapter(LayoutInflater.from(context))
adapter.addWidget { ListItemWidget() }        
adapter.addWidget { SeparatorWidget() }        
recyclerView.adapter = adapter
```
Of course it would be a good practise to inject the adapter and the providers through the DI mechanism of your choice. 
### Create a Widget
Your item views have to be mapped into ```Widget```s for the ```WidgetAdapter``` to match data types and view types. It uses ```setData(data: T)``` to update the data of a ```Widget```. Keep in mind, that your ```Widget``` might have been recycled and you might have to clean your view from its previous state.
```kotlin
class YourWidget: Widget<String>{
    override val events = EventDispatcher()

    private lateinit var textView: TextView
    
    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        textView = inflater.inflate(R.layout.your_item_layout) as TextView
        return textView
    }

    override fun setData(data: String) {
        textView.text = data
    }
}
```
### Use the SimpleWidget
For the implementation of your ```Widget```s you are free to use your preferred UI library like DataBinding, kotterknife and the like. However this library also comes with a convenient way to initialize your views and resources: the ```SimpleWidget```.
```kotlin
class YourWidget: SimpleWidget<String>(R.layout.your_item_layout){
    override val events = EventDispatcher()

    private val textView: TextView by viewId(R.id.text_view)
    private val imageView by viewId<ImageView>(R.id.image_view)
    
    private val string: String by resourceId(R.string.your_string)
    private val intArray by resourceId<Array<Int>>(R.string.your_string)
    private val color by colorResource(R.color.colorPrimary)
    private val dimension by dimensionInPixels(R.dimen.activity_horizontal_margin)

    override fun onViewCreated(view: View) {
        imageView.onClick { /* do something */ }
    }

    override fun setData(data: String) {
        textView.text = data
    }
}
```
### Handle events
There is always the question on how to propagate events from items in RecyclerViews. That is what the ```EventDispatcher``` is for. You raise those events in your widgets and can add listeners to the same events on the adapter. For each event you should create a small ```data class``` although if there is only one type of event in your adapter you could also use the payload as event. However wrapping your payload not only provides some context that helps readability it also enables you to raise different events with the same payload e.g. if you want to provide a select and a delete event from the same ```Widget```.

```kotlin
data class ItemClickedEvent(val data: String)

class YourWidget: SimpleWidget<String>(R.layout.your_item_layout){
    override val events = EventDispatcher()

    private lateinit val data: String

    override fun onViewCreated(view: View) {
        imageView.onClick { events.dispatch(ItemClickedEvent(data)) }
    }
    
    override fun setData(data: String) {
        this.data = data
        textView.text = data
    }
    ...
}
```
Listen to events:
```kotlin
adapter.events.subscribe<ItemClickedEvent> { println(it.data) }
```
If you want to be able to remove your listener manually later you can tag your listener and use this tag to remove it later. This tag can be any instance.
```kotlin
adapter.events.subscribe<ItemClickedEvent> (this) { println(it.data) }

// unsubscribe a single listener
adapter.events.unsubscribe<ItemClickedEvent>(this)
// unsubscribe all listeners of a certain tag
adapter.events.unsubscribeAllOf(this)
// unsubscribe all listeners
adapter.events.unsubscribeAll()
```

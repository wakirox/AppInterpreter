package it.sapienza.appinterpreter.model.view_model

import it.sapienza.appinterpreter.model.action.Action
import it.sapienza.appinterpreter.model.view_model.helper.Size
import it.sapienza.appinterpreter.model.view_model.helper.TextStyle
import it.sapienza.appinterpreter.model.view_model.helper.View
import it.sapienza.appinterpreter.model.view_model.helper.ViewElement

class TextView (
    var textSize : Float? = null,
    var size : Size? = null,
    var textStyle: TextStyle? = TextStyle.regular,
    var title : String?,
    var label : String? = null,
    mapping : String? = null
) : View(mapping), ViewElement {
    override var action: Action? = null
}

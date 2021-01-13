package it.sapienza.appinterpreter.model.view_model.helper

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.sapienza.appinterpreter.model.Layout
import it.sapienza.appinterpreter.model.ModelApplication
import it.sapienza.appinterpreter.model.view_model.*
import java.lang.Exception

import java.util.HashMap


class ViewObject(var type: ElementType.ViewElem? = null) {

    fun convert() : MView {
//        try {
            return type?.let {
                when (it) {
                    ElementType.ViewElem.layout -> {
                        jacksonObjectMapper().convertValue<Layout>(additionalProperties)
                    }
                    ElementType.ViewElem.text -> {
                        jacksonObjectMapper().convertValue<TextView>(additionalProperties)
                    }
                    ElementType.ViewElem.image -> {
                        jacksonObjectMapper().convertValue<ImageView>(additionalProperties)
                    }
                    ElementType.ViewElem.button -> {
                        jacksonObjectMapper().convertValue<ButtonView>(additionalProperties)
                    }
                    ElementType.ViewElem.form -> {
                        jacksonObjectMapper().convertValue<FormView>(additionalProperties)
                    }
                    ElementType.ViewElem.list -> {
                        jacksonObjectMapper().convertValue<ListView>(additionalProperties)
                    }
                    ElementType.ViewElem.pager -> {
                        jacksonObjectMapper().convertValue<PagerView>(additionalProperties)
                    }
                }
            } ?: run {
                jacksonObjectMapper().convertValue(additionalProperties)
            }
//        }catch (e : Exception){
//            throw Exception("Parse exception obj -> message ${e.message} type($type) attrs $additionalProperties",e.cause)
//        }

    }

    @JsonIgnore
    private val additionalProperties: MutableMap<String, Any> =
        HashMap()

    @JsonAnyGetter
    fun getAdditionalProperties(): Map<String, Any> {
        return additionalProperties
    }

    @JsonAnySetter
    fun setAdditionalProperty(name: String, value: Any) {
        additionalProperties[name] = value
    }
}
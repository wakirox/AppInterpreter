package it.sapienza.appinterpreter.model.event

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.json.JSONObject

open class Event(
    var mapping : String?,
    var data : MutableMap<Any?, Any?>?
){
    var dataObj: JSONObject?
        get() = data?.let {
            JSONObject(it)
        }
        set(value) {
            val typeRef: TypeReference<MutableMap<Any?, Any?>?> =
                object : TypeReference<MutableMap<Any?, Any?>?>() {}
            data = jacksonObjectMapper().readValue(value.toString(), typeRef)
        }
}
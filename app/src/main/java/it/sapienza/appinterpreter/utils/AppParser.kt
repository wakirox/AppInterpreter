package it.sapienza.appinterpreter.utils

import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.sapienza.appinterpreter.model.Layout
import it.sapienza.appinterpreter.model.ModelApplication
import it.sapienza.appinterpreter.model.event.*
import it.sapienza.appinterpreter.model.view_model.ListView
import it.sapienza.appinterpreter.model.view_model.helper.MView


class AppParser(val obj : String) {

    inline fun<T> measureTimeMillisPair(function: () -> T): T {
        val startTime = System.currentTimeMillis()
        val result: T = function.invoke()
        val endTime = System.currentTimeMillis()

        Log.d("TIME","Function took ${endTime - startTime}")
        return result
    }

    fun parseApplication2() : ModelApplication {

        /*
        * per facilitare la scrittura del modello si è deciso di dare la possibilità di riutilizzare determinati elementi
        * all'iterno del modello specificando solamente l'id di un elemento è possibile referenziarlo dalla lista generale dell'app
        * Per evitare problemi con la scrittura non è
        * possibile referenziare un elemento che si trova definito all'interno di un altro elemento*/

        val mapper = jacksonObjectMapper()

        val model = measureTimeMillisPair { mapper.readValue<ModelApplication>(obj) }

        model.actions.find { it.isEmpty() }?.let {
            throw Exception("Empty model.actions[id=${it.id}]")
        }

        model.views.find { it.isEmpty() }?.let {
            throw Exception("Empty model.views[id=${it.id}]")
        }

        model.actions.forEach { it.event?.let { ev -> findView(ev, model) } }

        if(!model.mainView.isEmpty()){findGenericView(model.mainView,model) }

//        model.initService?.let {
//            it.thenDo?.eventInstance?.let { ev -> findScreens(ev, model) }
//        }

        checkModelConsistency(model)

        return model

        //}
    }


//    fun parseApplication() : ModelApplication {
//
//        /*
//        * per facilitare la scrittura del modello si è deciso di dare la possibilità di riutilizzare determinati elementi
//        * all'iterno del modello specificando solamente l'id di un elemento è possibile referenziarlo dalla lista generale dell'app
//        * Per evitare problemi con la scrittura non è
//        * possibile referenziare un elemento che si trova definito all'interno di un altro elemento*/
//
//        val mapper = jacksonObjectMapper()
//        val model = mapper.readValue<ModelApplication>(obj)
//
//
//
////        model.initService?.let {
////            it.thenDo?.eventInstance?.let { ev -> analyzeEvent(ev, model) }
////        }
//
//        model.main.takeIf { !it.isEmpty() }?.let {
//            model.main = it.toReference()
//            model.screens.add(it)
//        }
//
//        model.screens.forEach {
//            model.layouts.addAll(it.layouts.filter { l->!l.isEmpty() })
//        }
//
//        model.layouts.iterator().forEach {
//            it.views.forEach { v ->
//                v.action?.event?.let {
//                    it.eventInstance?.let { ev -> analyzeEvent(ev, model) }
//                }
//            }
//        }
//
//        var toAddLayout = mutableListOf<Layout>()
//        model.layouts.forEach {
//            //converto le view in realtà
//            it.views.forEach{
//
//                if(it is ListView){
//                    toAddLayout.add(it.layout)
//                }
//
//                it.action?.takeIf { !it.isEmpty() }?.let {
//                    model.actions.add(it)
//                }
//            }
//
//        }
//
//        model.layouts.addAll(toAddLayout)
//
//        checkModelConsistency(model)
//
//        model.actions.filter { e->e.event?.eventInstance is ShowView}.onEach {
////            var ev = (it.event?.eventInstance as ShowScreen)
////            if(ev.screen.isEmpty() ){
////                ev.screen = model.screens.find { e->e.id == ev.screen.id }!!
////            }
//        }
//
//        //setto le azioni qualora siano solamente riferimenti
//        model.layouts.forEach { it.views.forEach{
//            it.action?.let { ac ->
//                if (ac.isEmpty()) {
//                    it.action = model.actions.find { ai -> ai.id == ac.id }!!
//                }
//            }
//        } }
//
//
////        if(model.main.isEmpty()){
////            model.main = model.screens.find { it.id == model.main.id }!!
////        }
//
//        return model
//    }

    private fun findView(
        ev: Event,
        model: ModelApplication
    ) {
        when(ev){
            is ShowView -> if(!ev.view.isEmpty()){
                findGenericView(ev.view, model)
            }
            is RESTService -> {
                ev.thenDo?.let { findView(it,model) }
            }
            is AlertMessage -> {
                ev.thenDoOK?.let { findView(it,model) }
                ev.thenDoKO?.let { findView(it,model) }
            }
        }
    }

    private fun findGenericView(view : MView, model: ModelApplication){
        when(view){
            is ListView -> {
                findGenericView(view.itemView,model)
            }
            is Layout -> {
                view.views.forEach {
                    findGenericView(it, model)
                }
            }
        }

        view.action?.event?.let {
            findView(it, model)
        }

        if(!view.isEmpty()){
            model.views.add(view)
        }

    }


    private fun checkModelConsistency(model: ModelApplication) {
        if(model.actions.any { it.isEmpty() }) throw Exception("A action model is empty")
        if(model.views.any { it.isEmpty() }) throw Exception("A view model is empty")
        if (model.actions.map { it.id }.toSet().size != model.actions.size) {
            throw Exception("You used a not unique layout id")
        }
    }

}
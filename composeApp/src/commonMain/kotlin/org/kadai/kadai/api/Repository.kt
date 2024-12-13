package org.kadai.kadai.api

import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver

class Repository(val name: String, val url: String, val description: String?) {
    companion object {
        val saver = mapSaver(save = {
            mapOf("name" to it.name, "url" to it.url, "description" to it.description)
        }, restore = {
            Repository(it["name"] as String,
                it["url"] as String,
                it["description"] as String?)
        })
        val listSaver = listSaver(save = { list ->
            list.map {
                mapOf("name" to it.name, "url" to it.url, "description" to it.description)
            }
        },
        restore = { saved ->
            val ret = arrayListOf<Repository>()
            saved.forEach {
                val repo = it as Map<String, *>
                ret.add(Repository(repo["name"] as String,
                    repo["url"] as String,
                    repo["description"] as String?))
            }
            return@listSaver ret.toList()
        })
    }
}
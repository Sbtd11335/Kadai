package org.kadai.kadai.api

import androidx.compose.runtime.saveable.listSaver

class IssueComment(val author: String?, val body: String) {
    companion object {
        val listSaver = listSaver(save = {
            it.map { issueComment ->
                mapOf("author" to issueComment.author, "body" to issueComment.body)
            }
        }, restore = {
            val ret = arrayListOf<IssueComment>()
            it.forEach { m ->
                val icMap = m as Map<String, *>
                ret.add(IssueComment(icMap["author"] as String?, icMap["body"] as String))
            }
            return@listSaver ret.toList()
        })
    }
}
package org.kadai.kadai.api

import androidx.compose.runtime.saveable.listSaver

class Issue(val title: String, val author: String?, val closed: Boolean, val labels: List<IssueLabel>) {
    companion object {
        val listSaver = listSaver(save = {
            it.map { issue ->
                mapOf("title" to issue.title,
                    "author" to issue.author,
                    "closed" to issue.closed,
                    "labels" to issue.labels)
            }
        }, restore = {
            val ret = arrayListOf<Issue>()
            it.forEach { issueMapOptional ->
                val issueMap = issueMapOptional as Map<String, *>
                ret.add(Issue(issueMap["title"] as String,
                    issueMap["author"] as String?,
                    issueMap["closed"] as Boolean,
                    issueMap["labels"] as List<IssueLabel>))
            }
            return@listSaver ret.toList()
        })
    }
}
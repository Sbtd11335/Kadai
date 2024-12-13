package org.kadai.kadai.api

import androidx.compose.ui.graphics.Color
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.kadai.kadai.CreateIssueMutation
import com.kadai.kadai.GithubIssueCommentsQuery
import com.kadai.kadai.GithubIssuesQuery
import com.kadai.kadai.GithubRepositoriesQuery
import com.kadai.kadai.type.CreateIssueInput
import org.kadai.kadai.ui.hexString

class Github(token: String) {
    private val client = ApolloClient.Builder()
        .serverUrl("https://api.github.com/graphql")
        .addHttpHeader("Authorization", "Bearer $token")
        .build()

    suspend fun getRepositories(nameFilter: String, isLoading: (Boolean) -> Unit): List<Repository> {
        isLoading(true)
        val repositories: ArrayList<Repository> = arrayListOf()
        var after: String? = null
        var currentPage = 0
        while (true) {
            val dataOptional =
                client.query(GithubRepositoriesQuery(100, Optional.present(after))).execute()
            if (dataOptional.data != null) {
                dataOptional.data!!.viewer.repositories.nodes?.let { nodes ->
                    nodes.forEach {
                        it?.let { node ->
                            val name = node.name
                            val url = node.url as String
                            val description = node.description
                            repositories.add(Repository(name, url, description))
                        }
                    }
                }
                after = dataOptional.data!!.viewer.repositories.pageInfo.endCursor
                if (dataOptional.data?.viewer?.repositories?.pageInfo?.hasNextPage == false)
                    break
            }
            currentPage++
        }
        isLoading(false)
        return repositories.filter { it.name.lowercase().indexOf(nameFilter.lowercase()) >= 0 }.reversed()
    }
    suspend fun getIssues(repository: Repository, isLoading: (Boolean) -> Unit): Pair<String, List<Issue>> {
        isLoading(true)
        var after: String? = null
        val issues: ArrayList<Issue> = arrayListOf()
        var repoId: String
        while(true) {
            val dataOptional = client.query(GithubIssuesQuery(repository.name, 100, Optional.present(after))).execute()
            if (dataOptional.data != null) {
                val repo = dataOptional.data!!.viewer.repository!!
                repoId = dataOptional.data!!.viewer.repository!!.id
                repo.issues.nodes?.forEach {
                    val issueLabels = arrayListOf<IssueLabel>()
                    it?.let { node ->
                        node.labels?.nodes?.let { labelsNodeOptional ->
                            labelsNodeOptional.forEach { labelOptional ->
                                labelOptional?.let { label ->
                                    issueLabels.add(IssueLabel(label.name, Color.hexString(label.color)))
                                }
                            }
                        }
                        issues.add(Issue(node.title, node.author?.login, node.closed, issueLabels))
                    }
                }
                after = repo.issues.pageInfo.endCursor
                if (!repo.issues.pageInfo.hasNextPage)
                    break
            }
        }
        isLoading(false)
        return Pair(repoId, issues.reversed())
    }
    suspend fun getIssueComments(repository: Repository, issueNum: Int, isLoading: (Boolean) -> Unit): List<IssueComment> {
        isLoading(true)
        var after: String? = null
        val issueComments = arrayListOf<IssueComment>()
        while(true) {
            val dataOptional = client.query(GithubIssueCommentsQuery(repository.name, issueNum, Optional.present(after))).execute()
            if (dataOptional.data != null && dataOptional.data!!.viewer.repository!!.issue != null) {
                val issue = dataOptional.data!!.viewer.repository!!.issue!!
                issueComments.add(IssueComment(issue.author?.login, issue.body))
                issue.comments.nodes?.let { nodeList ->
                    nodeList.forEach { nodeOptional ->
                        nodeOptional?.let { node ->
                            issueComments.add(IssueComment(node.author?.login, node.body))
                        }
                    }
                }
                if (!issue.comments.pageInfo.hasNextPage)
                    break
                after = issue.comments.pageInfo.endCursor
            }
        }
        isLoading(false)
        return issueComments
    }
    suspend fun createIssue(repoId: String, title: String, body: String) {
        val issueInput = CreateIssueInput(repositoryId = repoId, title = title, body = Optional.present(body))
        client.mutation(CreateIssueMutation(issueInput)).execute()
    }
}
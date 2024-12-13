package org.kadai.kadai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import org.kadai.kadai.AppSetting
import org.kadai.kadai.api.Github
import org.kadai.kadai.api.Issue
import org.kadai.kadai.api.IssueComment
import org.kadai.kadai.api.Repository

@Composable
fun IssueFrame(appSetting: AppSetting, repository: Repository, issueNum: Int, issue: Issue) {
    val nameTextStyle = TextStyle(color = Color.Foreground,
        fontSize = TextUnit(24f, TextUnitType.Sp))
    val showIssueInfo = rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val issueComments = rememberSaveable(stateSaver = IssueComment.listSaver) { mutableStateOf(listOf()) }
    val commentScroll = rememberLazyListState()

    BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(130.dp),
        contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.width(maxWidth - 20.dp).border(2.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp)).clickable {
            showIssueInfo.value = true
        }) {
            Column(modifier = Modifier.fillMaxSize().padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(issue.title, color = nameTextStyle.color, fontSize = nameTextStyle.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (issue.labels.isNotEmpty())
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        items(issue.labels) {
                            val textColor = if (isSystemInDarkTheme()) it.color else Color.Black
                            val borderColor = it.color
                            val backColor = if (isSystemInDarkTheme())
                                Color(it.color.red - .4f, it.color.green - .4f, it.color.blue - .4f)
                            else
                                Color.Transparent
                            Box(modifier = Modifier.background(color = backColor, RoundedCornerShape(17.dp))
                                .border(2.dp, color = borderColor, RoundedCornerShape(17.dp))) {
                                Text(it.name, modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp), color = textColor)
                            }
                        }
                    }
                else
                    Text("ラベルはありません。")
                Text(issue.author ?: "", color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }

    if (showIssueInfo.value) {
        LaunchedEffect(Unit) {
            issueComments.value = getIssueComments(appSetting, repository, issueNum) {
                isLoading = it
            }
        }
        Dialog(issue.title, onBack = { showIssueInfo.value = false }) {
            if (isLoading) {
                CircularProgressIndicator()
            }
            else {
                if (issueComments.value.isNotEmpty()) {
                    val ownerName = issueComments.value[0].author
                    LaunchedEffect(Unit) {
                        commentScroll.scrollToItem(Int.MAX_VALUE)
                    }
                    LazyColumn(modifier = Modifier.padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp), state = commentScroll) {
                        items(issueComments.value) {
                            ChatFrame(ownerName == it.author, it.author, it.body)
                        }
                    }
                }
            }
        }
    }
}

private suspend fun getIssueComments(appSetting: AppSetting, repository: Repository, issueNum: Int,
                                     isLoading: (Boolean) -> Unit): List<IssueComment> {
    val github = Github(appSetting.token.value)
    return github.getIssueComments(repository, issueNum) {
        isLoading(it)
    }
}
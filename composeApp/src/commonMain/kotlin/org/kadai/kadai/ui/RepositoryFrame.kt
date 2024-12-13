package org.kadai.kadai.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.kadai.kadai.AppSetting
import org.kadai.kadai.api.Github
import org.kadai.kadai.api.Issue
import org.kadai.kadai.api.Repository
import org.kadai.kadai.rangeList

@Composable
fun RepositoryFrame(appSetting: AppSetting, repository: Repository) {
    val nameTextStyle = TextStyle(color = Color.Foreground,
        fontSize = TextUnit(24f, TextUnitType.Sp))
    val showRepositoryInfo = rememberSaveable { mutableStateOf(false) }
    val showCreateIssue = rememberSaveable { mutableStateOf(false) }
    val nameTextStyle2 = TextStyle(color = Color.Foreground, fontWeight = FontWeight.Bold,
        fontSize = TextUnit(32f, TextUnitType.Sp))
    val clipBoard = LocalClipboardManager.current
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val repoId = rememberSaveable { mutableStateOf("") }
    val issues = rememberSaveable(stateSaver = Issue.listSaver) { mutableStateOf(listOf()) }
    val coroutineScope = rememberCoroutineScope()
    val errorState = rememberSaveable { mutableStateOf("") }
    val items = 5
    val issuesScrollState = rememberLazyListState()
    val currentPage = rememberSaveable { mutableIntStateOf(0) }
    val maxPage = rememberSaveable { mutableIntStateOf(0) }
    val loadIssues = {
        coroutineScope.launch {
            val result = repositoryInfo(appSetting, repository) {
                isLoading = it
            }
            repoId.value = result.first
            issues.value = result.second
            currentPage.intValue = 1
            maxPage.intValue = (issues.value.size - 1) / items + 1
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(200.dp),
        contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.width(maxWidth - 20.dp).border(2.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp)).clickable {
            showRepositoryInfo.value = true
        }) {
            Column(modifier = Modifier.fillMaxSize().padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(repository.name, color = nameTextStyle.color, fontSize = nameTextStyle.fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(repository.url, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(repository.description ?: "説明はありません。", color = Color.Foreground, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
    }
    if (showRepositoryInfo.value) {
        loadIssues()
        Dialog(repository.name, onBack = {
            showRepositoryInfo.value = false
        }) {
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                contentAlignment = Alignment.TopStart) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(repository.name, fontSize = nameTextStyle2.fontSize, color = nameTextStyle2.color,
                            fontWeight = nameTextStyle2.fontWeight)
                        Text(repository.url, color = Color.Gray, modifier = Modifier.clickable {
                            clipBoard.setText(AnnotatedString(repository.url))
                        })
                        Text("説明", color = Color.Foreground, fontWeight = FontWeight.Bold)
                        Text(repository.description ?: "説明はありません。", color = Color.Foreground)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text("Issues", fontSize = nameTextStyle2.fontSize, color = nameTextStyle2.color,
                                fontWeight = nameTextStyle2.fontWeight)
                            TextButton("追加", modifier = Modifier.width(55.dp).height(40.dp)) {
                                showCreateIssue.value = true
                            }
                        }
                        if (isLoading) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        else {
                            if (issues.value.isEmpty())
                                Text("Issuesはありません。", color = Color.Foreground)
                            else {
                                val displayList = rangeList(issues.value, (currentPage.value - 1) * items..<currentPage.value * items)
                                Box(modifier = Modifier.fillMaxSize()) {
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.padding(bottom = 50.dp)) {
                                        itemsIndexed(displayList) { index, iss ->
                                            IssueFrame(appSetting, repository, issues.value.size - index, iss)
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth().height(50.dp).align(Alignment.BottomCenter)) {
                                        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center) {
                                            item(span = { GridItemSpan(2) } ) {
                                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                                                    if (currentPage.intValue > 1)
                                                        Text("<", modifier = Modifier.clickable {
                                                            currentPage.intValue -= 1
                                                            coroutineScope.launch {
                                                                issuesScrollState.scrollToItem(0)
                                                            }
                                                        })
                                                }
                                            }
                                            item(span = { GridItemSpan(3) } ) {
                                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                    Text("${currentPage.intValue} / ${maxPage.value}")
                                                }
                                            }
                                            item(span = { GridItemSpan(2) } ) {
                                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                                                    if (currentPage.intValue < maxPage.intValue)
                                                        Text(">", modifier = Modifier.clickable {
                                                            currentPage.intValue += 1
                                                            coroutineScope.launch {
                                                                issuesScrollState.scrollToItem(0)
                                                            }
                                                        })
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showCreateIssue.value) {
        val title = rememberSaveable { mutableStateOf("") }
        val body = rememberSaveable { mutableStateOf("") }
        val boxTextStyle = TextStyle(color = Color.Foreground, fontSize = TextUnit(20f, TextUnitType.Sp))
        val tapped = rememberSaveable { mutableIntStateOf(0) }
        Dialog("Issueを作成", onBack = {
            showCreateIssue.value = false
            loadIssues()
        }) {
            Column(modifier = Modifier.padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("タイトル", fontSize = nameTextStyle2.fontSize, color = nameTextStyle2.color,
                    fontWeight = nameTextStyle2.fontWeight)
                TextField(title, "タイトルを入力", modifier = Modifier.fillMaxWidth().height(50.dp),
                    textStyle = boxTextStyle)
                Text("内容", fontSize = nameTextStyle2.fontSize, color = nameTextStyle2.color,
                    fontWeight = nameTextStyle2.fontWeight)
                TextField(body, "内容を入力", modifier = Modifier.fillMaxWidth().height(200.dp),
                    singleLine = false, textStyle = boxTextStyle)
                TextButton("作成", modifier = Modifier.fillMaxWidth().height(50.dp)) {
                    coroutineScope.launch {
                        tapped.intValue++
                        if (tapped.intValue == 1 && createIssue(appSetting, repoId.value, title.value, body.value, errorState)) {
                            showCreateIssue.value = false
                            loadIssues()
                        }
                    }
                }
            }
        }
    }
    if (errorState.value.isNotEmpty()) {
        Alert("エラー！", errorState.value) {
            errorState.value = ""
        }
    }
}

private suspend fun repositoryInfo(appSetting: AppSetting, repository: Repository,
                                   isLoading: (Boolean) -> Unit): Pair<String, List<Issue>> {
    val github = Github(appSetting.token.value)
    return github.getIssues(repository) {
        isLoading(it)
    }
}

private suspend fun createIssue(appSetting: AppSetting, repoId: String, title: String,
                                body: String, errorState: MutableState<String>): Boolean {
    val github = Github(appSetting.token.value)
    if (title.isEmpty()) {
        errorState.value = "タイトルが入力されておりません。"
        return false
    }
    if (body.isEmpty()) {
        errorState.value = "内容が入力されておりません。"
        return false
    }
    github.createIssue(repoId, title, body)
    return true
}
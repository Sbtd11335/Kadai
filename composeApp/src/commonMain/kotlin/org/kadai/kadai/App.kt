package org.kadai.kadai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RippleConfiguration
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kadai.composeapp.generated.resources.Res
import kadai.composeapp.generated.resources.gearspape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kadai.kadai.api.Github
import org.kadai.kadai.api.Repository
import org.kadai.kadai.ui.Alert
import org.kadai.kadai.ui.Background
import org.kadai.kadai.ui.Dialog
import org.kadai.kadai.ui.Foreground
import org.kadai.kadai.ui.IconButton
import org.kadai.kadai.ui.RepositoryFrame
import org.kadai.kadai.ui.TextButton
import org.kadai.kadai.ui.TextField

@Composable
@Preview
fun App() {
    val theme = if (isSystemInDarkTheme()) darkColors() else lightColors()
    MaterialTheme(theme) {
        val userToken = rememberSaveable { mutableStateOf("") }
        val appSetting = rememberSaveable(stateSaver = appSettingSaver) { mutableStateOf(AppSetting(userToken)) }
        val searchRepositoryName = rememberSaveable { mutableStateOf("") }
        val errorState = rememberSaveable { mutableStateOf("") }
        val showSettingDialog = rememberSaveable { mutableStateOf(false) }
        val boxTextStyle = TextStyle(color = Color.Foreground, fontSize = TextUnit(20f, TextUnitType.Sp))
        val coroutineScope = rememberCoroutineScope()
        val repositories = rememberSaveable(stateSaver = Repository.listSaver) { mutableStateOf(listOf()) }
        val isLoading = rememberSaveable { mutableStateOf(false) }
        val currentPage = rememberSaveable { mutableIntStateOf(1) }
        val maxPage = rememberSaveable { mutableIntStateOf(1) }
        val repositoryScrollState = rememberLazyListState()
        val items = 10

        Surface(modifier = Modifier.fillMaxWidth(),
            color = Color.Background) {
            Scaffold(topBar = {
                TopAppBar(title = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("リポジトリ", color = Color.White)
                    }
                }, actions = {
                    IconButton(painterResource(Res.drawable.gearspape),
                        color = Color.White) {
                        showSettingDialog.value = true
                    }
                })
            }) { paddingValues ->
                BoxWithConstraints(modifier = Modifier.fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding() + 10.dp),
                    contentAlignment = Alignment.TopCenter) {
                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        LazyVerticalGrid(columns = GridCells.Fixed(3), userScrollEnabled = false,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            item(span = { GridItemSpan(2) }) {
                                TextField(searchRepositoryName, label = "リポジトリを検索", modifier = Modifier.height(50.dp),
                                    textStyle = boxTextStyle)
                            }
                            item {
                                TextButton("検索", boxTextStyle, Modifier.height(50.dp)) {
                                    coroutineScope.launch {
                                        searchRepositories(appSetting.value, searchRepositoryName.value,
                                            repositories, maxPage, items, errorState, isLoading)
                                    }
                                    currentPage.intValue = 1
                                }
                            }
                        }
                        if (!isLoading.value) {
                            if (repositories.value.isNotEmpty()) {
                                val displayList = rangeList(repositories.value, (currentPage.value - 1) * items..<currentPage.value * items)
                                LazyColumn(modifier = Modifier.padding(bottom = 50.dp), state = repositoryScrollState) {
                                    items(displayList) {
                                        Box(modifier = Modifier.padding(bottom = 10.dp)) {
                                            RepositoryFrame(appSetting.value, it)
                                        }
                                    }
                                }
                            }
                            else if(appSetting.value.token.value.isNotEmpty() && searchRepositoryName.value.isNotEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text("リポジトリが見つかりませんでした。")
                                }
                            }
                        }
                        else {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth().height(50.dp).align(Alignment.BottomCenter)) {
                        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center) {
                            item(span = { GridItemSpan(2)} ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                                    if (currentPage.intValue > 1)
                                        Text("<", modifier = Modifier.clickable {
                                            currentPage.intValue -= 1
                                            coroutineScope.launch {
                                                repositoryScrollState.scrollToItem(0)
                                            }
                                        })
                                }
                            }
                            item(span = { GridItemSpan(3)} ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("${currentPage.intValue} / ${maxPage.value}")
                                }
                            }
                            item(span = { GridItemSpan(2)} ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                                    if (currentPage.intValue < maxPage.intValue)
                                        Text(">", modifier = Modifier.clickable {
                                            currentPage.intValue += 1
                                            coroutineScope.launch {
                                                repositoryScrollState.scrollToItem(0)
                                            }
                                        })
                                }
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
        if (showSettingDialog.value) {
            Dialog("設定", onBack = {
                showSettingDialog.value = false
            }) {
                Setting(appSetting)
            }
        }
    }
}

suspend fun searchRepositories(appSetting: AppSetting, searchRepositoryName: String,
                               repositories: MutableState<List<Repository>>,
                               maxPage: MutableState<Int>,
                               items: Int,
                               errorState: MutableState<String>,
                               isLoading: MutableState<Boolean>) {
    if (appSetting.token.value.isEmpty()) {
        errorState.value = "トークンが入力されておりません。\n設定画面からトークンを入力してください。"
        return
    }
    val github = Github(appSetting.token.value)
    val result = arrayListOf<Repository>()
    maxPage.value = 1
    github.getRepositories(searchRepositoryName) {
        isLoading.value = it
    }.forEach {
        result.add(it)
    }
    repositories.value = result
    maxPage.value = (repositories.value.size - 1) / items + 1
}
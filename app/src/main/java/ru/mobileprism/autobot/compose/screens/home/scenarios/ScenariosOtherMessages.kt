package ru.mobileprism.autobot.compose.screens.home.scenarios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import ru.mobileprism.autobot.R
import ru.mobileprism.autobot.compose.custom.*
import ru.mobileprism.autobot.compose.screens.home.AutoBotTextField
import ru.mobileprism.autobot.compose.screens.home.ListSpacer
import ru.mobileprism.autobot.compose.screens.home.MenuButton
import ru.mobileprism.autobot.utils.Constants

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScenariosOtherMessages(upPress: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    val pages = remember {
        mutableStateMapOf(0 to defaultSOtherMessage)
    }

    val pagerState = rememberPagerState(0)


    val onScrollToPage: (Int) -> Unit = {
        coroutineScope.launch {
            pagerState.animateScrollToPage(it)
        }
    }

    val onAddNewPage: () -> Unit = {
        pages.put(
            pages.keys.last().plus(1), SOtherMessagesData()
        ); onScrollToPage(pages.keys.last())
    }

//    val onDeleteCurrentPage: (Int) -> Unit = {
//        pages.remove()
//    }

    ScenariosLayout(topBar = {
        DefaultTopAppBar(title = stringResource(id = R.string.repeated_messages), upPress = upPress)
    }, onSaveClick = {}) {
        Column(
            modifier = Modifier.imePadding()
        ) {

            ScrollableTabRow(modifier = Modifier.fillMaxWidth(),
                //    .weight(1f, false)

                selectedTabIndex = pagerState.currentPage,
                divider = { Divider(modifier = Modifier.fillMaxWidth()) }) {
                pages.keys.forEach { key ->
                    Tab(selected = key == pagerState.currentPage,
                        onClick = { onScrollToPage(key) },
                        text = { Text((key + 1).toString()) })
                }
            }

            HorizontalPager(
                state = pagerState, count = pages.size, modifier = Modifier
            ) { page ->
                val currentPageData = remember { pages.getOrElse(page) { SOtherMessagesData() } }
                var firstMessage by remember {
                    mutableStateOf(currentPageData.primaryText)
                }
                var secondMessage by remember {
                    mutableStateOf(currentPageData.secondaryText)
                }
                var shouldUnite by remember {
                    mutableStateOf(currentPageData.shouldUnite)
                }
                var daysDelay by remember {
                    mutableStateOf(currentPageData.daysAfter)
                }
                ScenariosColumn(
                    modifier = Modifier
                        .padding(top = Constants.defaultPadding)
                        .verticalScroll(rememberScrollState())
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ScenariosTextField(title = stringResource(id = R.string.hello_text_description),
                        placeholder = stringResource(id = R.string.hello_text_placeholder),
                        value = firstMessage,
                        onValueChange = { firstMessage = it })

                    ScenariosTextField(title = stringResource(id = R.string.main_text_description),
                        placeholder = stringResource(id = R.string.main_text_placeholder),
                        value = secondMessage,
                        onValueChange = { secondMessage = it })

                    SwitchRow(text = "Объединить сообщения в одно?",
                        checked = shouldUnite,
                        onCheckedChange = { shouldUnite = it })

                    Column {
                        Text(text = "Через сколько дней после отправки первого сообщения, нужно отправить это сообщение?")
                        TimeSelectRow(
                            value = daysDelay.toString(),
                            onValueChange = { daysDelay = it.toInt() },
                            timeAmount = stringResource(R.string.days_after)
                        )
                    }

                    /*if (page != 0) {
                        MenuButton(
                            title = "Удалить это сообщение",
                            icon = Icons.Default.Delete,
//                            onClick = { onDeleteCurrentPage(page) }
                        )
                    }*/


                    MenuButton(
                        title = stringResource(R.string.add_new_message),
                        icon = Icons.Default.Add,
                        onClick = onAddNewPage
                    )

                    ListSpacer()
                }
            }
        }
    }
}

data class SOtherMessagesData(
    val primaryText: String = "",
    val secondaryText: String = "",
    val shouldUnite: Boolean = false,
    val daysAfter: Int = 5,
)

val defaultSOtherMessage = SOtherMessagesData(
    primaryText = "Добрый день!",
    secondaryText = "Наблюдаю за вашим авто, может быть встретимся, посмотрим его, поговорим о цене? Что скажете?",
    shouldUnite = false,
    daysAfter = 5,
)
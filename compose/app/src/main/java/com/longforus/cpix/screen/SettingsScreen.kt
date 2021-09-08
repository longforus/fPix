package com.longforus.cpix.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.longforus.cpix.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 33.dp, start = 15.dp, end = 15.dp, bottom = 15.dp)
    ) {
        val usingPaging by viewModel.usePaging.observeAsState()
        UsingPagingRow(usingPaging ?: false, viewModel)
    }
}

@Composable
private fun UsingPagingRow(using: Boolean, viewModel: MainViewModel) {
    Row(
        Modifier.height(60.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = "Change grid to paging list")
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(1f)) {
            Switch(checked = using, onCheckedChange = {
                viewModel.onUsingPagingChanged(it)
            })
        }

    }
}
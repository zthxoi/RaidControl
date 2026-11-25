package ru.checkraid.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.checkraid.app.R
import ru.checkraid.app.data.local.entity.Raid
import ru.checkraid.app.ui.viewmodel.RaidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaidsScreen(
    viewModel: RaidViewModel,
    onAddRaidClick: () -> Unit,
    onRaidClick: (Long) -> Unit
) {
    val raids by viewModel.raids.collectAsState()
    var raidToDelete by remember { mutableStateOf<Raid?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.title_raids)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddRaidClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add_raid)
                )
            }
        }
    ) { innerPadding ->
        if (raids.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.raids_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(raids, key = { it.id }) { raid ->
                    RaidCard(
                        raid = raid,
                        onClick = { onRaidClick(raid.id) },
                        onDeleteClick = { raidToDelete = raid }
                    )
                }
            }
        }
    }

    raidToDelete?.let { raid ->
        AlertDialog(
            onDismissRequest = { raidToDelete = null },
            title = { Text(stringResource(R.string.dialog_delete_raid_title)) },
            text = { Text(stringResource(R.string.dialog_delete_raid_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRaid(raid)
                        raidToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { raidToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun RaidCard(
    raid: Raid,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val statusText = when (raid.result) {
        Raid.RESULT_SUCCESS -> stringResource(R.string.status_success)
        Raid.RESULT_FAILED -> stringResource(R.string.status_failed)
        else -> stringResource(R.string.status_planned)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = raid.shopName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = onClick,
                    label = { Text(text = statusText) }
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.action_delete)
                    )
                }
            }
            if (raid.address.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = raid.address,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.raid_meta_format,
                    raid.date.ifBlank { "-" },
                    raid.inspectorName.ifBlank { "-" }
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
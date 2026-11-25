package ru.checkraid.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import ru.checkraid.app.R
import ru.checkraid.app.data.local.entity.ProductCheck
import ru.checkraid.app.data.local.entity.Raid
import ru.checkraid.app.ui.viewmodel.RaidViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRaidScreen(
    raidId: Long,
    viewModel: RaidViewModel,
    onBackClick: () -> Unit
) {
    val isEditing = raidId > 0L
    val settings by viewModel.userSettings.collectAsState()
    val checklists by viewModel.checklists.collectAsState()

    val savedProducts by remember(raidId) {
        if (isEditing) viewModel.getProductsForRaid(raidId) else flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    var pendingProducts by remember { mutableStateOf<List<ProductCheck>>(emptyList()) }
    var nextTempId by remember { mutableLongStateOf(-1L) }

    val products = if (isEditing) savedProducts else pendingProducts

    var shopName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var inspector by remember { mutableStateOf("") }
    var date by remember {
        mutableStateOf(SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()))
    }
    var result by remember { mutableStateOf(Raid.RESULT_PLANNED) }
    var notes by remember { mutableStateOf("") }
    var existingPhotoUri by remember { mutableStateOf<String?>(null) }

    var showProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductCheck?>(null) }
    var showChecklistPicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val shopRequiredError = stringResource(R.string.error_shop_name_required)

    LaunchedEffect(raidId) {
        if (isEditing) {
            viewModel.getRaidById(raidId)?.let { loaded ->
                shopName = loaded.shopName
                address = loaded.address
                inspector = loaded.inspectorName
                date = loaded.date
                result = loaded.result
                notes = loaded.notes
                existingPhotoUri = loaded.receiptPhotoUri
            }
        } else {
            inspector = settings.defaultInspector
            if (settings.defaultCity.isNotBlank() && address.isBlank()) {
                address = "${settings.defaultCity}, "
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (isEditing) R.string.title_edit_raid else R.string.title_add_raid
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = { Text(stringResource(R.string.add_raid_shop_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(stringResource(R.string.add_raid_address_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = inspector,
                onValueChange = { inspector = it },
                label = { Text(stringResource(R.string.add_raid_inspector_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text(stringResource(R.string.add_raid_date_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.add_raid_result_label),
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = result == Raid.RESULT_PLANNED,
                    onClick = { result = Raid.RESULT_PLANNED },
                    label = { Text(stringResource(R.string.status_planned)) }
                )
                FilterChip(
                    selected = result == Raid.RESULT_SUCCESS,
                    onClick = { result = Raid.RESULT_SUCCESS },
                    label = { Text(stringResource(R.string.status_success)) }
                )
                FilterChip(
                    selected = result == Raid.RESULT_FAILED,
                    onClick = { result = Raid.RESULT_FAILED },
                    label = { Text(stringResource(R.string.status_failed)) }
                )
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.add_raid_notes_label)) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = stringResource(R.string.add_raid_products_section),
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        editingProduct = null
                        showProductDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(
                        text = stringResource(R.string.action_add_product),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                OutlinedButton(
                    onClick = { showChecklistPicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Checklist, contentDescription = null)
                    Text(
                        text = stringResource(R.string.action_from_checklist),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            if (products.isEmpty()) {
                Text(
                    text = stringResource(R.string.add_raid_products_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                products.forEach { product ->
                    ProductCheckCard(
                        product = product,
                        onClick = {
                            editingProduct = product
                            showProductDialog = true
                        },
                        onDeleteClick = {
                            if (isEditing) {
                                viewModel.deleteProductCheck(product)
                            } else {
                                pendingProducts = pendingProducts.filterNot { it.id == product.id }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (shopName.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar(shopRequiredError) }
                        return@Button
                    }
                    val raid = Raid(
                        id = if (isEditing) raidId else 0L,
                        date = date.trim(),
                        address = address.trim(),
                        shopName = shopName.trim(),
                        result = result,
                        inspectorName = inspector.trim(),
                        notes = notes.trim(),
                        receiptPhotoUri = existingPhotoUri
                    )
                    if (isEditing) {
                        viewModel.updateRaid(raid, onDone = onBackClick)
                    } else {
                        viewModel.createRaidWithProducts(raid, pendingProducts, onDone = onBackClick)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.action_save))
            }
        }
    }

    if (showProductDialog) {
        ProductCheckDialog(
            initial = editingProduct,
            onDismiss = { showProductDialog = false },
            onSave = { name, price, expectedPrice, isMatch ->
                val current = editingProduct
                if (current == null) {
                    if (isEditing) {
                        viewModel.addProductCheck(
                            ProductCheck(
                                raidId = raidId,
                                productName = name,
                                price = price,
                                expectedPrice = expectedPrice,
                                isMatch = isMatch
                            )
                        )
                    } else {
                        val tempProduct = ProductCheck(
                            id = nextTempId,
                            raidId = 0L,
                            productName = name,
                            price = price,
                            expectedPrice = expectedPrice,
                            isMatch = isMatch
                        )
                        nextTempId -= 1L
                        pendingProducts = pendingProducts + tempProduct
                    }
                } else {
                    val updated = current.copy(
                        productName = name,
                        price = price,
                        expectedPrice = expectedPrice,
                        isMatch = isMatch
                    )
                    if (isEditing) {
                        viewModel.updateProductCheck(updated)
                    } else {
                        pendingProducts = pendingProducts.map { if (it.id == current.id) updated else it }
                    }
                }
                showProductDialog = false
            }
        )
    }

    if (showChecklistPicker) {
        AlertDialog(
            onDismissRequest = { showChecklistPicker = false },
            title = { Text(stringResource(R.string.dialog_pick_checklist_title)) },
            text = {
                if (checklists.isEmpty()) {
                    Text(stringResource(R.string.dialog_pick_checklist_empty))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        checklists.forEach { checklist ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isEditing) {
                                            val newItems = checklist.items.map { itemName ->
                                                ProductCheck(
                                                    raidId = raidId,
                                                    productName = itemName,
                                                    price = 0.0,
                                                    expectedPrice = 0.0,
                                                    isMatch = true
                                                )
                                            }
                                            viewModel.addProductChecks(newItems)
                                        } else {
                                            val added = checklist.items.map { itemName ->
                                                val item = ProductCheck(
                                                    id = nextTempId,
                                                    raidId = 0L,
                                                    productName = itemName,
                                                    price = 0.0,
                                                    expectedPrice = 0.0,
                                                    isMatch = true
                                                )
                                                nextTempId -= 1L
                                                item
                                            }
                                            pendingProducts = pendingProducts + added
                                        }
                                        showChecklistPicker = false
                                    }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = checklist.name,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = stringResource(R.string.checklist_items_count, checklist.items.size),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showChecklistPicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun ProductCheckCard(
    product: ProductCheck,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = stringResource(R.string.product_price_format, product.price, product.expectedPrice),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(
                        if (product.isMatch) R.string.product_status_match else R.string.product_status_mismatch
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (product.isMatch) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.action_delete)
                )
            }
        }
    }
}

@Composable
private fun ProductCheckDialog(
    initial: ProductCheck?,
    onDismiss: () -> Unit,
    onSave: (name: String, price: Double, expectedPrice: Double, isMatch: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(initial?.productName ?: "") }
    var priceText by remember { mutableStateOf(initial?.price?.takeIf { it > 0 }?.toString() ?: "") }
    var expectedText by remember { mutableStateOf(initial?.expectedPrice?.takeIf { it > 0 }?.toString() ?: "") }
    var isMatch by remember { mutableStateOf(initial?.isMatch ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (initial == null) R.string.dialog_add_product_title else R.string.dialog_edit_product_title
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.product_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.replace(',', '.') },
                    label = { Text(stringResource(R.string.product_actual_price_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = expectedText,
                    onValueChange = { expectedText = it.replace(',', '.') },
                    label = { Text(stringResource(R.string.product_expected_price_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isMatch = !isMatch }
                ) {
                    Checkbox(
                        checked = isMatch,
                        onCheckedChange = { isMatch = it }
                    )
                    Text(
                        text = stringResource(R.string.product_match_checkbox),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            name.trim(),
                            priceText.toDoubleOrNull() ?: 0.0,
                            expectedText.toDoubleOrNull() ?: 0.0,
                            isMatch
                        )
                    }
                }
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
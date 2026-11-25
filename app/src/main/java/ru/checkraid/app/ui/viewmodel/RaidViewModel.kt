package ru.checkraid.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.checkraid.app.data.local.AppDatabase
import ru.checkraid.app.data.local.entity.Checklist
import ru.checkraid.app.data.local.entity.ProductCheck
import ru.checkraid.app.data.local.entity.Raid
import ru.checkraid.app.data.prefs.UserPreferences
import ru.checkraid.app.data.prefs.UserSettings

class RaidViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val raidDao = db.raidDao()
    private val productCheckDao = db.productCheckDao()
    private val checklistDao = db.checklistDao()
    private val userPreferences = UserPreferences(application)

    val raids: StateFlow<List<Raid>> = raidDao.getAllRaids()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val checklists: StateFlow<List<Checklist>> = checklistDao.getAllChecklists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _userSettings = MutableStateFlow(userPreferences.loadSettings())
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()

    // --- Raid CRUD ---
    suspend fun getRaidById(id: Long): Raid? = raidDao.getRaidById(id)

    fun createRaidWithProducts(raid: Raid, products: List<ProductCheck>, onDone: () -> Unit) {
        viewModelScope.launch {
            val newRaidId = raidDao.insert(raid)
            if (products.isNotEmpty()) {
                val linkedProducts = products.map { it.copy(id = 0, raidId = newRaidId) }
                productCheckDao.insertAll(linkedProducts)
            }
            onDone()
        }
    }

    fun updateRaid(raid: Raid, onDone: () -> Unit) {
        viewModelScope.launch {
            raidDao.update(raid)
            onDone()
        }
    }

    fun deleteRaid(raid: Raid) {
        viewModelScope.launch {
            raidDao.delete(raid)
        }
    }

    // --- ProductCheck CRUD ---
    fun getProductsForRaid(raidId: Long): Flow<List<ProductCheck>> {
        return productCheckDao.getProductsByRaid(raidId)
    }

    fun addProductCheck(productCheck: ProductCheck) {
        viewModelScope.launch {
            productCheckDao.insert(productCheck)
        }
    }

    fun addProductChecks(products: List<ProductCheck>) {
        viewModelScope.launch {
            productCheckDao.insertAll(products)
        }
    }

    fun updateProductCheck(productCheck: ProductCheck) {
        viewModelScope.launch {
            productCheckDao.update(productCheck)
        }
    }

    fun deleteProductCheck(productCheck: ProductCheck) {
        viewModelScope.launch {
            productCheckDao.delete(productCheck)
        }
    }

    // --- Checklist CRUD ---
    fun addChecklist(name: String, items: List<String>) {
        viewModelScope.launch {
            checklistDao.insert(Checklist(name = name.trim(), items = items))
        }
    }

    fun updateChecklist(checklist: Checklist) {
        viewModelScope.launch {
            checklistDao.update(checklist)
        }
    }

    fun deleteChecklist(checklist: Checklist) {
        viewModelScope.launch {
            checklistDao.delete(checklist)
        }
    }

    // --- SharedPreferences ---
    fun saveUserSettings(settings: UserSettings) {
        userPreferences.saveSettings(settings)
        _userSettings.value = userPreferences.loadSettings()
    }
}
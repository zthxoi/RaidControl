package ru.checkraid.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import ru.checkraid.app.R

sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector? = null
) {
    data object Raids : Screen("raids", R.string.nav_raids, Icons.Default.ListAlt)
    data object Checklists : Screen("checklists", R.string.nav_checklists, Icons.Default.Checklist)
    data object Map : Screen("map", R.string.nav_map, Icons.Default.Map)
    data object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)

    data object AddRaid : Screen("add_raid", R.string.title_add_raid) {
        const val ARG_RAID_ID = "raidId"
        const val routeWithArg = "add_raid?$ARG_RAID_ID={$ARG_RAID_ID}"
        fun createRoute(raidId: Long = -1L): String = "add_raid?$ARG_RAID_ID=$raidId"
    }

    companion object {
        val bottomNavItems = listOf(Raids, Checklists, Map, Settings)
    }
}
package com.longforus.cpix.bean

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class IconScreens(val route: String, val label:String, val icon: ImageVector) {

    //Bottom Nav
    object Image : IconScreens("image", "Image", Icons.Outlined.Image)
    object Video : IconScreens("video", "Video", Icons.Outlined.VideoLibrary)
    object Favorite : IconScreens("favorite", "Favorite", Icons.Outlined.Favorite)
    object Setting : IconScreens("setting", "Setting", Icons.Outlined.Settings)
}
package com.longforus.cpix

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.longforus.cpix.bean.IconScreens
import com.longforus.cpix.screen.ImageScreen
import com.longforus.cpix.ui.theme.CPixTheme
import com.longforus.cpix.ui.theme.Purple500
import com.longforus.cpix.util.StatusBarUtil
import com.longforus.cpix.viewmodel.ImageViewModel

class MainActivity : AppCompatActivity() {
    private val imageVm by viewModels<ImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.transparentStatusBar(this)
        setContent {
            AppMainNavigation()
        }
    }


    @Composable
    fun AppMainNavigation() {

        val navController = rememberNavController()

        NavHost(navController, startDestination = IconScreens.Image.route) {


            // Bottom Nav
            composable(IconScreens.Image.route) {
                ImageScreen(navController)
            }
            composable(IconScreens.Video.route) {
                ScaffoldScreen(navController) {
                    Text(text = IconScreens.Video.label)
                }
            }
            composable(IconScreens.Favorite.route) {
                ScaffoldScreen(navController) {
                    Text(text = IconScreens.Favorite.label)
                }
            }
            composable(IconScreens.Setting.route) {
                ScaffoldScreen(navController) {
                    Text(text = IconScreens.Setting.label)
                }
            }
        }
    }

    @Composable
    fun ImageScreen(navController: NavHostController) {
        ScaffoldScreen(navController = navController,
            float = {
                FloatingActionButton(onClick = { /*TODO*/ }) {
                    Icon(painter = rememberVectorPainter(image = Icons.Filled.Search), contentDescription = null)
                }
            }
        ) {
            val usePaging by imageVm.usePaging.observeAsState()
            val imageFragment = remember {
                ImageScreen(imageVm)
            }
            imageFragment.ImageScreen(usePaging = usePaging ?: false)
        }
    }

    @Composable
    fun ScaffoldScreen(navController: NavHostController, float: @Composable (() -> Unit)? = null, screen: @Composable () -> Unit) {

        val bottomNavigationItems = listOf(
            IconScreens.Image,
            IconScreens.Video,
            IconScreens.Favorite,
            IconScreens.Setting
        )
        CPixTheme {
            Scaffold(topBar = {},
                bottomBar = { BottomAppNavBar(navController, bottomNavigationItems) },
                content = { screen() },
                floatingActionButton = { float?.invoke() }
            )
        }


    }


    @Composable
    fun BottomAppNavBar(navController: NavHostController, bottomNavigationItems: List<IconScreens>) {
        BottomAppBar(
            backgroundColor = Color.White,
            contentColor = Purple500,
            elevation = 10.dp,
        ) {
            bottomNavigationItems.forEach { screen ->
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                BottomNavigationItem(
                    icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    label = {
                        if (currentRoute == screen.route) {
                            Text(text = screen.label)
                        }
                    },
                    alwaysShowLabel = false
                )
            }
        }

    }


}

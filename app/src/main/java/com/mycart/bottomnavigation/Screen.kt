package com.mycart.bottomnavigation

sealed class Screen(val route: String) {
  //  object StoreList : Screen("store_list")
    object Home : Screen("home")
   object Orders : Screen("orders")
    /* object Others : Screen("others")*/
}

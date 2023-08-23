package com.yenaly.han1meviewer.ui.fragment.home

import android.os.Bundle
import com.yenaly.han1meviewer.R
import com.yenaly.han1meviewer.databinding.FragmentPageListBinding
import com.yenaly.han1meviewer.ui.viewmodel.MyListViewModel
import com.yenaly.han1meviewer.util.showAlertDialog
import com.yenaly.yenaly_libs.base.YenalyFragment

/**
 * @project Han1meViewer
 * @author Yenaly Liew
 * @time 2022/07/04 004 22:43
 */
class MyPlayListFragment : YenalyFragment<FragmentPageListBinding, MyListViewModel>() {
    override fun initData(savedInstanceState: Bundle?) {

        addMenu(R.menu.menu_my_list_toolbar, viewLifecycleOwner) { menuItem ->
            when (menuItem.itemId) {
                R.id.tb_help -> {
                    requireContext().showAlertDialog {
                        setTitle("使用注意！")
                        setMessage("我還沒做這塊，如果有想幫忙的非常歡迎！")
                        setPositiveButton("OK", null)
                    }
                    return@addMenu true
                }
            }
            return@addMenu false
        }
    }

    override fun liveDataObserve() {

    }
}
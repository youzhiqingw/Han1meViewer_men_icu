package com.yenaly.han1meviewer.ui.fragment.search

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Checkable
import androidx.core.util.isNotEmpty
import androidx.fragment.app.activityViewModels
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.lxj.xpopupext.listener.TimePickerListener
import com.lxj.xpopupext.popup.TimePickerPopup
import com.yenaly.han1meviewer.R
import com.yenaly.han1meviewer.SEARCH_YEAR_RANGE_END
import com.yenaly.han1meviewer.SEARCH_YEAR_RANGE_START
import com.yenaly.han1meviewer.databinding.PopUpFragmentSearchOptionsBinding
import com.yenaly.han1meviewer.logic.model.SearchOption.Companion.get
import com.yenaly.han1meviewer.ui.popup.HTimePickerPopup
import com.yenaly.han1meviewer.ui.viewmodel.SearchViewModel
import com.yenaly.han1meviewer.util.showAlertDialog
import com.yenaly.yenaly_libs.base.YenalyBottomSheetDialogFragment
import com.yenaly.yenaly_libs.utils.mapToArray
import java.util.Calendar
import java.util.Date

/**
 * @project Han1meViewer
 * @author Yenaly Liew
 * @time 2023/08/08 008 17:04
 */
class SearchOptionsPopupFragment :
    YenalyBottomSheetDialogFragment<PopUpFragmentSearchOptionsBinding>() {

    companion object Tags {
        private const val POP_UP_BORDER_RADIUS = 36F
    }

    private val viewModel by activityViewModels<SearchViewModel>()

    private var genres: Array<String>? = null
    private var sortOptions: Array<String>? = null
    private var durations: Array<String>? = null

    // Popups

    private val timePickerPopup: TimePickerPopup
        get() {
            val date = Calendar.getInstance().also {
                val year = viewModel.year
                val month = viewModel.month
                if (year != null && month != null) {
                    it.set(year, month, 0)
                }
            }
            val popup = HTimePickerPopup(requireContext())
                .apply {
                    setMode(TimePickerPopup.Mode.YM)
                    setYearRange(SEARCH_YEAR_RANGE_START, SEARCH_YEAR_RANGE_END)
                    setDefaultDate(date)
                    setTimePickerListener(object : TimePickerListener {
                        override fun onCancel() = Unit
                        override fun onTimeChanged(date: Date) = Unit

                        override fun onTimeConfirm(date: Date, view: View?) {
                            val calendar = Calendar.getInstance()
                            calendar.time = date
                            when (mode) {
                                TimePickerPopup.Mode.YM -> {
                                    viewModel.year = calendar.get(Calendar.YEAR)
                                    viewModel.month = calendar.get(Calendar.MONTH) + 1
                                }

                                else -> {
                                    viewModel.year = calendar.get(Calendar.YEAR)
                                    viewModel.month = null
                                }
                            }
                            initOptionsChecked()
                        }
                    })
                }
            return XPopup.Builder(requireContext()).setOptionsCheckedCallback()
                .borderRadius(POP_UP_BORDER_RADIUS)
                .isDarkTheme(true)
                .asCustom(popup) as TimePickerPopup
        }

    override fun initData(savedInstanceState: Bundle?, dialog: Dialog) {
        initOptionsChecked()
        initClick()
    }

    private fun initOptionsChecked() {
        binding.brand.isChecked = viewModel.brandMap.isNotEmpty()
        binding.sortOption.isChecked = viewModel.sort != null
        binding.duration.isChecked = viewModel.duration != null
        binding.tag.isChecked = viewModel.tagMap.isNotEmpty()
        binding.type.isChecked = viewModel.genre != null
        binding.releaseDate.isChecked = viewModel.year != null || viewModel.month != null
    }

    private fun initClick() {
        binding.type.apply {
            setOnClickListener {
                // typePopup.show()
                if (genres == null) {
                    genres = viewModel.genres.mapToArray { it.value }
                }
                requireContext().showAlertDialog {
                    val index = viewModel.genres.indexOfFirst {
                        it.searchKey == viewModel.genre
                    }
                    setTitle(R.string.type)
                    setSingleChoiceItems(genres, index) { _, which ->
                        viewModel.genre = viewModel.genres.getOrNull(which)?.searchKey
                        initOptionsChecked()
                    }
                    setPositiveButton(R.string.save, null)
                    setNeutralButton(R.string.reset) { _, _ ->
                        viewModel.genre = null
                        initOptionsChecked()
                    }
                    setOnDismissListener {
                        initOptionsChecked()
                    }
                }
            }
            setOnLongClickListener lc@{
                showClearAllTagsDialog {
                    viewModel.genre = null
                    initOptionsChecked()
                }
                return@lc true
            }
        }
        binding.brand.apply {
            setOnClickListener {
                HMultiChoicesDialog(context, R.string.brand, hasSingleItem = true).apply {
                    addTagScope(null, viewModel.brands, spanCount = 2)
                }.apply {
                    loadSavedTags(viewModel.brandMap)
                    setOnSaveListener {
                        viewModel.brandMap = collectCheckedTags()
                        initOptionsChecked()
                        it.dismiss()
                    }
                    setOnResetListener {
                        clearAllChecks()
                        initOptionsChecked()
                    }
                }.show()
            }
            setOnLongClickListener lc@{
                showClearAllTagsDialog {
                    viewModel.brandMap.clear()
                    initOptionsChecked()
                }
                return@lc true
            }
        }
        binding.tag.apply {
            setOnClickListener {
                HMultiChoicesDialog(context, R.string.tag).apply {
                    addTagScope(
                        R.string.video_attr,
                        viewModel.tags[R.string.video_attr],
                        spanCount = 1
                    )
                    addTagScope(
                        R.string.relationship,
                        viewModel.tags[R.string.relationship],
                        spanCount = 2
                    )
                    addTagScope(
                        R.string.characteristics,
                        viewModel.tags[R.string.characteristics]
                    )
                    addTagScope(
                        R.string.appearance_and_figure,
                        viewModel.tags[R.string.appearance_and_figure]
                    )
                    addTagScope(
                        R.string.story_plot,
                        viewModel.tags[R.string.story_plot]
                    )
                    addTagScope(
                        R.string.sex_position,
                        viewModel.tags[R.string.sex_position]
                    )
                }.apply {
                    loadSavedTags(viewModel.tagMap)
                    setOnSaveListener {
                        viewModel.tagMap = collectCheckedTags()
                        initOptionsChecked()
                        it.dismiss()
                    }
                    setOnResetListener {
                        clearAllChecks()
                        initOptionsChecked()
                    }
                }.show()
            }
            setOnLongClickListener lc@{
                showClearAllTagsDialog {
                    viewModel.tagMap.clear()
                    initOptionsChecked()
                }
                return@lc true
            }
        }
        binding.sortOption.apply {
            setOnClickListener {
                // sortOptionPopup.show()
                if (sortOptions == null) {
                    sortOptions = viewModel.sortOptions.mapToArray { it.value }
                }
                requireContext().showAlertDialog {
                    val index = viewModel.sortOptions.indexOfFirst {
                        it.searchKey == viewModel.sort
                    }
                    setTitle(R.string.sort_option)
                    setSingleChoiceItems(sortOptions, index) { _, which ->
                        viewModel.sort = viewModel.sortOptions.getOrNull(which)?.searchKey
                        initOptionsChecked()
                    }
                    setPositiveButton(R.string.save, null)
                    setNeutralButton(R.string.reset) { _, _ ->
                        viewModel.sort = null
                        initOptionsChecked()
                    }
                    setOnDismissListener {
                        initOptionsChecked()
                    }
                }
            }
            setOnLongClickListener lc@{
                showClearAllTagsDialog {
                    viewModel.sort = null
                    initOptionsChecked()
                }
                return@lc true
            }
        }
        binding.duration.apply {
            setOnClickListener {
                // durationPopup.show()
                if (durations == null) {
                    durations = viewModel.durations.mapToArray { it.value }
                }
                requireContext().showAlertDialog {
                    val index = viewModel.durations.indexOfFirst {
                        it.searchKey == viewModel.duration
                    }
                    setTitle(R.string.duration)
                    setSingleChoiceItems(durations, index) { _, which ->
                        viewModel.duration = viewModel.durations.getOrNull(which)?.searchKey
                        initOptionsChecked()
                    }
                    setPositiveButton(R.string.save, null)
                    setNeutralButton(R.string.reset) { _, _ ->
                        viewModel.duration = null
                        initOptionsChecked()
                    }
                    setOnDismissListener {
                        initOptionsChecked()
                    }
                }
            }
            setOnLongClickListener lc@{
                showClearAllTagsDialog {
                    viewModel.duration = null
                    initOptionsChecked()
                }
                return@lc true
            }
        }
        binding.releaseDate.apply {
            setOnClickListener {
                timePickerPopup.show()
            }
            setOnLongClickListener lc@{
                showClearAllTagsDialog {
                    viewModel.year = null
                    viewModel.month = null
                    initOptionsChecked()
                }
                return@lc true
            }
        }
    }

    private inline fun Checkable.showClearAllTagsDialog(crossinline action: () -> Unit) {
        if (isChecked) {
            requireContext().showAlertDialog {
                setTitle(R.string.alert)
                setMessage(R.string.alert_cancel_all_tags)
                setPositiveButton(R.string.confirm) { _, _ -> action.invoke() }
                setNegativeButton(R.string.cancel, null)
            }
        }
    }

    private fun XPopup.Builder.setOptionsCheckedCallback() = apply {
        setPopupCallback(object : SimpleCallback() {
            override fun beforeDismiss(popupView: BasePopupView?) {
                initOptionsChecked()
            }
        })
    }
}
package org.evidyaloka.common.view.multiSelectionSpinner


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Instrumentation
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import org.evidyaloka.common.R
import java.util.*

/**
 * @author Madhankumar
 * created on 02-02-2021
 */
class MultiSpinnerSearch : AppCompatSpinner,
    DialogInterface.OnCancelListener {
    private var highlightSelected = false
    private var highlightColor =
        ContextCompat.getColor(context, R.color.list_selected)
    private var textColor = Color.GRAY
    private var limit = -1
    private var selected = 0
    private var defaultText: String? = ""
    private var spinnerTitle: String? = ""
    private var emptyTitle = "Not Found!"
    private var searchHint = "Type to search"
    private var clearText: String? = "Clear All"
    var isColorSeparation = false
    var isShowSelectAllButton = false
    private var listener: MultiSpinnerListener? = null
    private var limitListener: LimitExceedListener? = null
    private var adapter: MyAdapter? = null
    private var items: List<KeyPairBoolData> = listOf()
    var isSearchEnabled = true

    constructor(context: Context?) : super(context!!) {}
    constructor(arg0: Context, arg1: AttributeSet?) : super(
        arg0,
        arg1
    ) {
        val a = arg0.obtainStyledAttributes(arg1, R.styleable.MultiSpinnerSearch)
        for (i in 0 until a.indexCount) {
            val attr = a.getIndex(i)
            if (attr == R.styleable.MultiSpinnerSearch_hintText) {
                hintText = a.getString(attr)
                spinnerTitle = hintText
                defaultText = spinnerTitle
                break
            } else if (attr == R.styleable.MultiSpinnerSearch_highlightSelected) {
                highlightSelected = a.getBoolean(attr, false)
            } else if (attr == R.styleable.MultiSpinnerSearch_highlightColor) {
                highlightColor = a.getColor(
                    attr,
                    ContextCompat.getColor(
                        context,
                        R.color.list_selected
                    )
                )
            } else if (attr == R.styleable.MultiSpinnerSearch_textColor) {
                textColor = a.getColor(attr, Color.GRAY)
            } else if (attr == R.styleable.MultiSpinnerSearch_clearText) {
                setClearText(a.getString(attr))
            }
        }

        //Log.i(TAG, "spinnerTitle: " + spinnerTitle);
        a.recycle()
    }

    constructor(
        arg0: Context?,
        arg1: AttributeSet?,
        arg2: Int
    ) : super(arg0!!, arg1, arg2) {
    }

    var hintText: String?
        get() = spinnerTitle
        set(hintText) {
            spinnerTitle = hintText
            defaultText = spinnerTitle
        }

    fun setClearText(clearText: String?) {
        this.clearText = clearText
    }

    fun setLimit(limit: Int, listener: LimitExceedListener?) {
        this.limit = limit
        limitListener = listener
        isShowSelectAllButton = false // if its limited, select all default false.
    }

    val selectedItems: List<KeyPairBoolData>
        get() {
            val selectedItems: MutableList<KeyPairBoolData> =
                ArrayList()
            for (item in items) {
                if (item.isSelected) {
                    selectedItems.add(item)
                }
            }
            return selectedItems
        }

    val selectedIds: List<Long>
        get() {
            val selectedItemsIds: MutableList<Long> =
                ArrayList()
            for ((id, _, isSelected) in items) {
                if (isSelected) {
                    selectedItemsIds.add(id)
                }
            }
            return selectedItemsIds
        }

    override fun onCancel(dialog: DialogInterface) {
        // refresh text on spinner
        Handler(Looper.getMainLooper()).postDelayed({
            Thread(Runnable {
                val inst = Instrumentation()
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
            }).start()
        }, 200)
        val spinnerBuffer = StringBuilder()
        val selectedData =
            ArrayList<KeyPairBoolData>()
        for (i in items.indices) {
            val currentData = items[i]
            if (currentData.isSelected) {
                selectedData.add(currentData)
                spinnerBuffer.append(currentData.name)
                spinnerBuffer.append(", ")
            }
        }
        var spinnerText: String? = spinnerBuffer.toString()
        spinnerText = if (spinnerText!!.length > 2) spinnerText.substring(
            0,
            spinnerText.length - 2
        ) else hintText
        val adapterSpinner = ArrayAdapter(
            context,
            R.layout.textview_for_spinner,
            arrayOf(spinnerText)
        )
        setAdapter(adapterSpinner)
        if (adapter != null) adapter!!.notifyDataSetChanged()
        listener?.onItemsSelected(selectedData)
        /**
         * To hide dropdown which is already opened at the time of performClick...
         * This code will hide automatically and no need to tap by user.
         */



    }

    override fun performClick(): Boolean {
        super.performClick()
        builder = AlertDialog.Builder(context)
        builder!!.setTitle(spinnerTitle)
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.alert_dialog_listview_search, null)
        builder!!.setView(view)
        val listView =
            view.findViewById<ListView>(R.id.alertSearchListView)
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView.isFastScrollEnabled = false
        adapter = MyAdapter(context, items)
        listView.adapter = adapter
        val emptyText = view.findViewById<TextView>(R.id.empty)
        emptyText.text = emptyTitle
        listView.emptyView = emptyText
        val editText = view.findViewById<EditText>(R.id.alertSearchEditText)
        if (isSearchEnabled) {
            editText.visibility = View.VISIBLE
            editText.hint = searchHint
            editText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    adapter!!.filter.filter(s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {}
            })
        } else {
            editText.visibility = View.GONE
        }
        /**
         * For selected items
         */
        selected = 0
        for (i in items.indices) {
            if (items[i].isSelected) selected++
        }

        /*
        Added Select all Dialog Button.
         */if (isShowSelectAllButton && limit == -1) {
            builder!!.setNeutralButton(
                R.string.selectall
            ) { dialog: DialogInterface, which: Int ->
                for (i in adapter!!.arrayList.indices) {
                    adapter!!.arrayList[i].isSelected = true
                }
                adapter!!.notifyDataSetChanged()
                // To call onCancel listner and set title of selected items.
                dialog.cancel()
            }
        }
        builder!!.setPositiveButton(
            R.string.ok
        ) { dialog: DialogInterface, which: Int ->
            //Log.i(TAG, " ITEMS : " + items.size());
            dialog.cancel()
        }
        builder!!.setNegativeButton(
            clearText
        ) { dialog, which -> //Log.i(TAG, " ITEMS : " + items.size());
            for (i in adapter!!.arrayList.indices) {
                adapter!!.arrayList[i].isSelected = false
            }
            adapter!!.notifyDataSetChanged()
            dialog.cancel()
        }
        builder!!.setOnCancelListener(this)
        Objects.requireNonNull(builder!!.show().getWindow())
            ?.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        return true
    }

    fun setItems(
        items: List<KeyPairBoolData>,
        listener: MultiSpinnerListener?
    ) {
        this.items = items
        this.listener = listener
        val spinnerBuffer = StringBuilder()
        for (i in items.indices) {
            if (items[i].isSelected) {
                spinnerBuffer.append(items[i].name)
                spinnerBuffer.append(", ")
            }
        }
        if (spinnerBuffer.length > 2) defaultText =
            spinnerBuffer.toString().substring(0, spinnerBuffer.toString().length - 2)
        val adapterSpinner = ArrayAdapter(
            context,
            R.layout.textview_for_spinner,
            arrayOf(defaultText)
        )
        setAdapter(adapterSpinner)
    }

    fun setEmptyTitle(emptyTitle: String) {
        this.emptyTitle = emptyTitle
    }

    fun setSearchHint(searchHint: String) {
        this.searchHint = searchHint
    }

    interface LimitExceedListener {
        fun onLimitListener(data: KeyPairBoolData?)
    }

    //Adapter Class
    inner class MyAdapter internal constructor(
        context: Context?,
        var arrayList: List<KeyPairBoolData>
    ) : BaseAdapter(), Filterable {
        val mOriginalValues : List<KeyPairBoolData>
        val inflater: LayoutInflater
        override fun getCount(): Int {
            return arrayList.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View? {
//            //Log.i(TAG, "getView() enter");
            var convertView = convertView
            val holder: ViewHolder
            if (convertView == null) {
                holder =
                    ViewHolder()
                convertView = inflater.inflate(R.layout.item_listview_multiple, parent, false)
                holder.textView = convertView.findViewById(R.id.alertTextView)
                holder.checkBox =
                    convertView.findViewById(R.id.alertCheckbox)
                convertView.tag = holder
            } else {
                holder =
                    convertView.tag as ViewHolder
            }
            var background = R.color.white
            if (isColorSeparation) {
                val backgroundColor: Int =
                    if (position % 2 == 0) R.color.list_even else R.color.list_odd
                background = backgroundColor
                convertView?.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        backgroundColor
                    )
                )
            }
            val data = arrayList!![position]
            holder.textView!!.text = data.name
            holder.checkBox!!.isChecked = data.isSelected
            convertView?.setOnClickListener { v: View ->
                if (data.isSelected) { // deselect
                    selected--
                } else { // selected
                    selected++
                    if (selected > limit && limit > 0) {
                        --selected // select with limit
                        if (limitListener != null) limitListener!!.onLimitListener(data)
                        return@setOnClickListener
                    }
                }
                val temp =
                    v.tag as ViewHolder
                temp.checkBox!!.isChecked = !temp.checkBox!!.isChecked
                data.isSelected = !data.isSelected
                //Log.i(TAG, "On Click Selected Item : " + data.getName() + " : " + data.isSelected());
                notifyDataSetChanged()
            }
            if (data.isSelected) {
                holder.textView!!.setTextColor(textColor)
                if (highlightSelected) {
                    holder.textView!!.setTypeface(null, Typeface.BOLD)
                    convertView?.setBackgroundColor(highlightColor)
                } else {
                    convertView?.setBackgroundColor(Color.WHITE)
                }
            } else {
                holder.textView!!.setTypeface(null, Typeface.NORMAL)
                convertView?.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        background
                    )
                )
            }
            holder.checkBox!!.tag = holder
            return convertView
        }

        @SuppressLint("DefaultLocale")
        override fun getFilter(): Filter {
            return object : Filter() {
                override fun publishResults(
                    constraint: CharSequence,
                    results: FilterResults
                ) {
                    arrayList =
                        results.values as List<KeyPairBoolData> // has the filtered values
                    notifyDataSetChanged() // notifies the data with new filtered values
                }

                override fun performFiltering(constraint: CharSequence): FilterResults {
                    var constraint: CharSequence? = constraint
                    val results =
                        FilterResults() // Holds the results of a filtering operation in values
                    val FilteredArrList: MutableList<KeyPairBoolData> =
                        ArrayList()


                    /*
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     **/if (constraint == null || constraint.length == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues!!.size
                        results.values = mOriginalValues
                    } else {
                        constraint = constraint.toString().toLowerCase()
                        for (i in mOriginalValues!!.indices) {
                            //Log.i(TAG, "Filter : " + mOriginalValues.get(i).getName() + " -> " + mOriginalValues.get(i).isSelected());
                            val data = mOriginalValues[i].name
                            if (data.toLowerCase().contains(constraint.toString())) {
                                FilteredArrList.add(mOriginalValues[i])
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size
                        results.values = FilteredArrList
                    }
                    return results
                }
            }
        }

        private inner class ViewHolder {
            var textView: TextView? = null
            var checkBox: CheckBox? = null
        }

        init {
            mOriginalValues = arrayList
            inflater = LayoutInflater.from(context)
        }
    }

    companion object {
        var builder: AlertDialog.Builder? = null
    }
}
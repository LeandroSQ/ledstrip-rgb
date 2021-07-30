package quevedo.soares.leandro.automation.view.led.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import quevedo.soares.leandro.automation.databinding.ItemCardBinding

data class LEDStyleItem(
	var text: String,
	@DrawableRes var background: Int
)

class LEDStyleAdapter(private val recyclerView: RecyclerView, private var items: List<LEDStyleItem>, private val listener: (LEDStyleItem) -> Unit) : RecyclerView.Adapter<LEDStyleAdapter.ViewHolder>() {

	private val inflater by lazy { LayoutInflater.from(recyclerView.context) }

	init {
		this.recyclerView.adapter = this
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(ItemCardBinding.inflate(inflater, parent, false))
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(this.items[position], this.listener)
	}

	override fun getItemCount() = items.size

	class ViewHolder(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {

		fun bind(item: LEDStyleItem, listener: (LEDStyleItem) -> Unit) {
			binding.idBackground.setBackgroundResource(item.background)
			binding.idText.text = item.text
			binding.root.setOnClickListener {
				listener(item)
			}
		}

	}

}
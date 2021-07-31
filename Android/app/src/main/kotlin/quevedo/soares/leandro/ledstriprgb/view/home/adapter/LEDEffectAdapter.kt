package quevedo.soares.leandro.ledstriprgb.view.home.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import quevedo.soares.leandro.ledstriprgb.R
import quevedo.soares.leandro.ledstriprgb.data.model.LEDEffectItem
import quevedo.soares.leandro.ledstriprgb.databinding.ItemCardBinding

class LEDEffectAdapter(private val recyclerView: RecyclerView, private var items: List<LEDEffectItem>, private val listener: (LEDEffectItem) -> Unit) : RecyclerView.Adapter<LEDEffectAdapter.ViewHolder>() {

	private val inflater by lazy { LayoutInflater.from(recyclerView.context) }

	init {
		this.recyclerView.adapter = this
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(ItemCardBinding.inflate(inflater, parent, false))
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(this.recyclerView.resources, this.items[position], this.listener)
	}

	override fun getItemCount() = items.size

	class ViewHolder(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {

		fun bind(resources: Resources, item: LEDEffectItem, listener: (LEDEffectItem) -> Unit) {
			binding.idBackground.setBackgroundResource(item.background)
			binding.idText.text = resources.getStringArray(R.array.effects)[item.animation.id]
			binding.root.setOnClickListener {
				listener(item)
			}
		}

	}

}
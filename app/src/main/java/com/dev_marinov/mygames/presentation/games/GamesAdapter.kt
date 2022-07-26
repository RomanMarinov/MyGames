package com.dev_marinov.mygames.presentation.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dev_marinov.mygames.R
import com.dev_marinov.mygames.databinding.ItemGamesBinding
import com.dev_marinov.mygames.domain.game.Game
import com.squareup.picasso.Picasso

class GamesAdapter(
    private val onClick: (id: String) -> Unit
) : ListAdapter<Game, GamesAdapter.ViewHolder>(GamesDiffUtilCallBack()) {

    private var games: List<Game> = ArrayList()

    override fun submitList(list: List<Game>?) {
        super.submitList(list)
        list?.let { this.games = it.toList() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItemBinding = ItemGamesBinding.inflate(inflater, parent, false)
        return ViewHolder(listItemBinding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(games[position])
    }

    override fun getItemCount() = games.size

    inner class ViewHolder(private val binding: ItemGamesBinding, onClick: (id: String) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game?) {
            binding.itemGames = game

            Picasso.get()  // установка главной картинки игры
                .load(game!!.image.toString())
                .resize(500, 300) // обязательно свои размеры (т.к. оригинал большой)
                .placeholder(R.drawable.picture_not_available)
                .centerCrop()
                .into(binding.imgMain) // -----> картинка

            binding.cardView.setOnClickListener {
                onClick(game.id)
            }

            // Метод executePendingBindings используется, чтобы биндинг не откладывался,
            // а выполнился как можно быстрее. Это критично в случае с RecyclerView.
            binding.executePendingBindings() // обязательно
        }

    }
}

class GamesDiffUtilCallBack : DiffUtil.ItemCallback<Game>() {
    override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
        return oldItem == newItem
    }

}

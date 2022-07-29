package com.example.android.simulator.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.simulator.databinding.MatchItemBinding;
import com.example.android.simulator.domain.Match;
import com.example.android.simulator.ui.DetailActivity;

import java.util.List;

//adapter tem a função de pegar uma estrutura de dados como por ex nossa lista de partidas
//que seralizamos da API e fazer com que essas infos sejam adptadas pros nossos elementos visuais do
//nosso layout
public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {
    //preciso extender essas bibliotecas e precisamos ter esses mesmos comportamentos
    private List<Match> matches;//lista de partidas

    public MatchesAdapter(List<Match> matches) {//gero um construtor para receber as listas como
        //parametro
        this.matches = matches;
    }

    public List<Match> getMatches() {
        return matches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //esse é o layout que eu peguei segundo o contexto
        MatchItemBinding binding = MatchItemBinding.inflate(layoutInflater, parent, false);
        //quando utilizamos um viewbinding em um recyclerview temos que respeitar a hirerarquia
        //dos elementos, attachToParent que deixamos sempre como false
        //esse metodo instancia o viewholder mas o viewholder precisa o item de binding para ser
        //instanciado, por isso usamos o elemento de binding dando inflate recebendo o layoutinflate
        //
        return new ViewHolder(binding);//instancia do meu viewholder
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        //contexto da nossa aplicação pegando o holder.elemento da raiz e dar um getcontext
        Match match = matches.get(position);//a partir desse elemento de holder de uma instancia
        //e da posição eu consigo entender qual partida(no caso) que está rolando

        // Adapta os dados da partida (recuperada da API) para o nosso layout.
        Glide.with(context).load(match.getHomeTeam().getImage()).circleCrop().into(holder.binding.ivHomeTeam);
        //load tem que ser url e essa url esta na minha partida e vai carregar onde? into(holder.binding.ivHomeTeam)
        //carrega a foto do time da casa
        holder.binding.tvHomeTeamName.setText(match.getHomeTeam().getName());//o nome do time da casa
        //estou atribuindo a textview correspondente
        if (match.getHomeTeam().getScore() != null) {
            holder.binding.tvHomeTeamScore.setText(String.valueOf(match.getHomeTeam().getScore()));
        }
        Glide.with(context).load(match.getAwayTeam().getImage()).circleCrop().into(holder.binding.ivAwayTeam);
        //load tem que ser url e essa url esta na minha partida e vai carregar onde? into(holder.binding.ivAwayTeam)
        //carrega a foto do time visitante
        holder.binding.tvAwayTeamName.setText(match.getAwayTeam().getName());//o nome do time visitante
        //estou atribuindo a textview correspondente
        if (match.getAwayTeam().getScore() != null) {
            holder.binding.tvAwayTeamScore.setText(String.valueOf(match.getAwayTeam().getScore()));
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(DetailActivity.Extras.MATCH, match);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return matches.size();//tamanho da lista
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MatchItemBinding binding;

        public ViewHolder(MatchItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

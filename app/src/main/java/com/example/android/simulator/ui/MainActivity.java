package com.example.android.simulator.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android.simulator.R;
import com.example.android.simulator.data.MatchesApi;
import com.example.android.simulator.databinding.ActivityMainBinding;
import com.example.android.simulator.domain.Match;
import com.example.android.simulator.ui.adapter.MatchesAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MatchesApi matchesApi;
    private MatchesAdapter matchesAdapter = new MatchesAdapter(Collections.emptyList());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupHttpClient();
        setupMatchesList();
        setupMatchesRefresh();
        setupFloatingActionButton();
    }

    private void setupHttpClient() {
        //esse método instancia o retrofit com o builder do proprio retrofit
        //e estabelece a conexão com a base url que dá um get
        //da classe MatchesApi
        Retrofit retrofit = new Retrofit.Builder() //instancio o retrofit
                .baseUrl("https://evelynberto.github.io/matches-simulator-API/")
                .addConverterFactory(GsonConverterFactory.create())//biblioteca responsável pela
                //serelização e deserelização de json no contexto do projeto android com Gson
                //ela já tem sua propria converterFactory e dou um create
                .build();
        matchesApi = retrofit.create(MatchesApi.class);//criar um retrofit a partir da nossa interface
    }

    private void setupMatchesList() {
        binding.rvMatches.setHasFixedSize(true);//tem um tamanho fixo
        binding.rvMatches.setLayoutManager(new LinearLayoutManager(this));
        //setamos um layout manager, espera um elemento de contexto e como estamos na activity
        //o this pode ser esse contexto
        binding.rvMatches.setAdapter(matchesAdapter);//setando o adapter para receber a lista
        //de partidas

        findMatchesFromApi();
        //tanto a ação de inicializar a pagina e dar o swipe refresh vao precisar consumir a api
        //fizemos a extração do método response e refreshing para um só
    }

    private void setupMatchesRefresh() {
        binding.srlMatches.setOnRefreshListener(this::findMatchesFromApi);
        //metodo do swipe refresh
        //quando alguem dar o swipe chamamos o findMatchesFromApi
    }

    private void setupFloatingActionButton() {
        //acessar os elementos de visão do conceito de viewbinding do jetpack
        binding.fabSimulate.setOnClickListener(view -> {
            //atribuindo o evento de clique, o view é o botao
            view.animate().rotationBy(360).setDuration(500).setListener(new AnimatorListenerAdapter() {
                //animando o botao e fazendo ele girar, atribuindo o listener pra ter uma callback
                //quando terminar a animação
                @Override
                public void onAnimationEnd(Animator animation) {
                    //animação vem do jetpack
                    //esse metodo faz com que quando acabe a animação a pagina atualize
                    Random random = new Random();
                    for (int i = 0; i < matchesAdapter.getItemCount(); i++) {
                        Match match = matchesAdapter.getMatches().get(i);
                        match.getHomeTeam().setScore(random.nextInt(match.getHomeTeam().getStars() + 1));
                        match.getAwayTeam().setScore(random.nextInt(match.getAwayTeam().getStars() + 1));
                        matchesAdapter.notifyItemChanged(i);
                    }
                }
            });
        });
    }

    private void findMatchesFromApi() {
        binding.srlMatches.setRefreshing(true);
        //a barra de carregar (swipe) antes de consumir a api é true
        matchesApi.getMatches().enqueue(new Callback<List<Match>>() {
            //metodo enqueue que me da um callback de consumo
            @Override
            public void onResponse(@NonNull Call<List<Match>> call, @NonNull Response<List<Match>> response) {
                if (response.isSuccessful()) { //se a reposta for sucesso pego as partidas
                    List<Match> matches = response.body();
                    //lista de partidas que vem do corpo das minhas partidas
                    matchesAdapter = new MatchesAdapter(matches);

                    binding.rvMatches.setAdapter(matchesAdapter);
                } else {
                    showErrorMessage();
                    //mensagem de erro
                }
                binding.srlMatches.setRefreshing(false);
                //depois que der sucesso ela para
            }

            @Override
            public void onFailure(@NonNull Call<List<Match>> call, @NonNull Throwable t) {
                showErrorMessage();
                binding.srlMatches.setRefreshing(false);
                //se falhar ela para
            }
        });
    }

    private void showErrorMessage() {
        Snackbar.make(binding.fabSimulate, R.string.error_api, Snackbar.LENGTH_LONG).show();
        //componente nativo do Material Desing passando uma view através do binding usando
        //o floating action buton uma string com o erro e definindo um tempo
    }
}

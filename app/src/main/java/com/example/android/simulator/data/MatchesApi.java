package com.example.android.simulator.data;

import com.example.android.simulator.domain.Match;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
//camada de acesso a dados
public interface MatchesApi {
    @GET("matches.json")//que recurso da minha api ele tem que acessar?
        //nome do arquivo
    Call<List<Match>> getMatches();//espero que o retorno desse call seja uma
    //lista de match(partidas)
}

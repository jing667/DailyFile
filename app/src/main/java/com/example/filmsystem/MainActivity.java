package com.example.filmsystem;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;  // 加载指示器
    private TextView errorTextView;   // 显示错误信息
    private static final String API_KEY = "f051ef0d"; // 替换为你的 API 密钥
    private static final String BASE_URL = "https://www.omdbapi.com/"; // 替换为你的 API 基础 URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 RecyclerView 和其他视图
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        movieAdapter = new MovieAdapter();
        recyclerView.setAdapter(movieAdapter);

        progressBar = findViewById(R.id.progress_bar); // 加载指示器
        errorTextView = findViewById(R.id.error_text_view); // 错误文本视图

        // 调用方法以获取电影数据
        fetchMovies();
    }

    private void fetchMovies() {
        // 显示加载指示器
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE); // 隐藏错误文本视图

        // 初始化 Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 创建 API 服务
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<MovieResponse> call = apiService.getMovies(API_KEY, "Avengers"); // 这里可以替换为任何你想查询的电影名

        // 异步请求
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                // 隐藏加载指示器
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getMovies();
                    if (movies != null && !movies.isEmpty()) {
                        movieAdapter.setMovies(movies);
                        Log.d("MainActivity", "获取到电影数据: " + movies.size());

                    } else {
                        Log.d("MainActivity", "没有获取到电影数据");
                        errorTextView.setText("没有获取到电影数据");
                        errorTextView.setVisibility(View.VISIBLE); // 显示错误信息
                    }
                } else {
                    Log.d("MainActivity", "响应失败: " + response.message());
                    errorTextView.setText("响应失败: " + response.message());
                    errorTextView.setVisibility(View.VISIBLE); // 显示错误信息
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // 隐藏加载指示器
                progressBar.setVisibility(View.GONE);
                Log.e("MainActivity", "网络请求失败", t);
                errorTextView.setText("网络请求失败: " + t.getMessage());
                errorTextView.setVisibility(View.VISIBLE); // 显示错误信息
            }
        });
    }
}

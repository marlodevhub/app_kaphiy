package com.marlodev.app_android.ui.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.R;
import com.marlodev.app_android.utils.SessionManager;

import java.util.ArrayList;

public class AdminHomeFragment extends Fragment {
    private TextView tvUserName, tvUserEmail, tvUserRole;
    private MaterialButton btnLogout;

    private BarChart barChart;

    private PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflamos el layout del fragment
        View root = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Inicializamos views
        initViews(root);

        // Edge-to-edge (opcional, igual que en la Activity)
        ViewCompat.setOnApplyWindowInsetsListener(root.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cargamos datos del usuario
        loadUserData();

        //Grafico de barras
        setupBarChart();

        setupPieChart();

        // Configuramos el botón de logout
        setupLogoutButton();

        return root;
    }

    private void initViews(View root) {
        tvUserName = root.findViewById(R.id.tvUserName);
        tvUserEmail = root.findViewById(R.id.tvUserEmail);
        tvUserRole = root.findViewById(R.id.tvUserRole);
        btnLogout = root.findViewById(R.id.btnLogout);
        barChart = root.findViewById(R.id.barChart);
        pieChart = root.findViewById(R.id.pieChart);

        FrameLayout btnPerfilUsuario = root.findViewById(R.id.btnPerfilUsuario);
        btnPerfilUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UserAdminActivity.class);
            startActivity(intent);
        });


        FrameLayout btnNotificaciones = root.findViewById(R.id.btnNotificaciones);

        btnNotificaciones.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(requireContext(), NotificationsActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error al abrir notificaciones: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace(); // Verás el error en Logcat
            }
        });

        // 🔔 Actualizar el número del badge
        TextView badgeNumero = root.findViewById(R.id.badgeNumero);

        // Simulación de lista de notificaciones (puedes reemplazar con tu fuente real)
        ArrayList<String> listaNotificaciones = new ArrayList<>();
        listaNotificaciones.add("Nuevo pedido recibido");
        listaNotificaciones.add("Stock bajo en Capuccino Grande");
        listaNotificaciones.add("Producto editado exitosamente");

        int cantidad = listaNotificaciones.size();
        badgeNumero.setText(String.valueOf(cantidad));
        badgeNumero.setVisibility(cantidad == 0 ? View.GONE : View.VISIBLE);






    }

    private void loadUserData() {
        SessionManager session = SessionManager.getInstance(requireContext());
        String email = session.getEmail();
        String role = session.getRole();

        // Nombre simulado a partir del correo
        String username = (email != null && email.contains("@"))
                ? email.substring(0, email.indexOf("@"))
                : "Delivery";

        tvUserName.setText(username);
        tvUserEmail.setText(email != null ? email : "correo@ejemplo.com");
        tvUserRole.setText("Rol: " + (role != null ? role : "Desconocido"));
    }


    private void setupBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 20));
        entries.add(new BarEntry(1, 60));
        entries.add(new BarEntry(2, 100));
        entries.add(new BarEntry(3, 140));

        BarDataSet dataSet = new BarDataSet(entries, "Ventas por Hora");
        dataSet.setColor(Color.parseColor("#3F51B5"));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        String[] horas = {"8 a.m.",  "12 p.m.", "4 p.m.", "8 p.m."};
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(horas));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setTextSize(10f);
        barChart.getXAxis().setDrawGridLines(false);

        barChart.getAxisLeft().setTextSize(10f);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.invalidate(); // refresca el gráfico
    }


    private void setupPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(45f, "Café"));
        entries.add(new PieEntry(25f, "Panes"));
        entries.add(new PieEntry(20f, "Frappes"));
        entries.add(new PieEntry(10f, "Postres"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                Color.parseColor("#FF9800"), // Café
                Color.parseColor("#2196F3"), // Panes
                Color.parseColor("#4CAF50"), // Frappes
                Color.parseColor("#795548")  // Postres
        );
        dataSet.setDrawValues(false); // ❌ Oculta los números dentro del gráfico

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.setDrawEntryLabels(false); // ❌ Oculta los nombres dentro del gráfico
        pieChart.setDrawHoleEnabled(false); // opcional: sin hueco central
        pieChart.getDescription().setEnabled(false); // ❌ Oculta descripción
        pieChart.getLegend().setEnabled(false); // ❌ Oculta leyenda automática

        pieChart.invalidate(); // refresca el gráfico
    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            // Borrar sesión
            SessionManager.getInstance(requireContext()).clear();

            // Redirigir a MainActivity
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
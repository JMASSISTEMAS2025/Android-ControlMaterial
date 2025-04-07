package com.example.controlmaterial11;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.example.controlmaterial11.GenerarReporteActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Este es el layout base
    }

    @Override
    public void setContentView(View view) {
        // Infla el layout base del drawer
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base, null);

        // Configura el contenedor y el contenido de la vista
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);

        // Establece el contenido de la actividad a drawerLayout
        super.setContentView(drawerLayout);

        // Configura el Toolbar
        Toolbar toolbar = drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // Configura el NavigationView
        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Configura el ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_navigation_drawer,
                R.string.close_navigation_drawer
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configura el nombre de usuario en el NavigationView
        View headerView = navigationView.getHeaderView(0);
        TextView txtUsuario = headerView.findViewById(R.id.txt_usuario);

        // Obtener el nombre de usuario desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", "Nombre Usuario"); // Valor predeterminado
        txtUsuario.setText(userName);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = item.getItemId();

        if (id == R.id.cerrar_sesion) {
            // Limpiar las preferencias del usuario
            SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();  // Limpia las preferencias del usuario
            editor.apply();

            // Cerrar la aplicación completamente
            finishAffinity();  // Cierra todas las actividades de la pila
            System.exit(0);    // Fuerza el cierre del proceso (opcional, no siempre necesario)

            return true;
        }

        manejarAccionesNavegacion(id);
        return true;
    }

    private void manejarAccionesNavegacion(int itemId) {
        if (itemId == R.id.nav_inicio) {
            iniciarNuevaActividad(InicioActivity.class);
        } else if (itemId == R.id.nav_generar_reporte) {
            iniciarNuevaActividad(GenerarReporteActivity.class);
        } else if (itemId == R.id.eliminar_reporte) {
            iniciarNuevaActividad(Eliminar_reporteActivity.class);
        } else if (itemId == R.id.editar_reporte) {
            iniciarNuevaActividad(Editar_reporte_Activity.class);
        } else if (itemId == R.id.informacion) {
            iniciarNuevaActividad(Acerca_de_Activity.class);
        } else if (itemId == R.id.sincronizar_reportes) {
            //iniciarNuevaActividad(SincronizarActivity.class);
        }
    }

    private void iniciarNuevaActividad(Class<?> destinoActividad) {
        startActivity(new Intent(this, destinoActividad));
        overridePendingTransition(0, 0);
    }
}
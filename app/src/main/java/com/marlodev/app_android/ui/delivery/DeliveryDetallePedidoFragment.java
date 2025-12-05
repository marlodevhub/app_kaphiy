package com.marlodev.app_android.ui.delivery;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.model.Pedido;

import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.style.layers.SymbolLayer;
import org.maplibre.android.style.sources.GeoJsonSource;
import org.maplibre.geojson.Feature;
import org.maplibre.geojson.Point;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.maplibre.android.style.layers.PropertyFactory.iconImage;

public class DeliveryDetallePedidoFragment extends Fragment {

    private TextView txtTitulo, txtCliente, txtDireccion;
    private MapView mapView;
    private MapLibreMap mapLibreMap;
    private Button btnVerRuta, btnChatCliente, btnChatTienda;
    private Pedido pedido;
    private LatLng ubicacionCliente;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (MapLibre.getInstance(context.getApplicationContext()) == null) {
                MapLibre.getInstance(context.getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delivery_detalle_pedido, container, false);

        // Referencias UI
        txtTitulo = view.findViewById(R.id.txtTitulo);
        txtCliente = view.findViewById(R.id.txtCliente);
        txtDireccion = view.findViewById(R.id.txtDireccion);
        btnVerRuta = view.findViewById(R.id.btnVerRuta);
        btnChatCliente = view.findViewById(R.id.btnChatCliente);
        btnChatTienda = view.findViewById(R.id.btnChatTienda);
        mapView = view.findViewById(R.id.mapView);

        // Botón de retroceso
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        // Obtener datos del pedido
        if (getArguments() != null) {
            pedido = (Pedido) getArguments().getSerializable("pedido");

            if (pedido != null) {
                txtTitulo.setText("Pedido de " + pedido.getCliente());
                txtCliente.setText("Cliente: " + pedido.getCliente());
                txtDireccion.setText(pedido.getDireccion());

                // Obtener coordenadas en hilo secundario
                new Thread(() -> {
                    ubicacionCliente = obtenerCoordenadasDesdeDireccion(pedido.getDireccion());
                    if (ubicacionCliente != null && mapLibreMap != null) {
                        requireActivity().runOnUiThread(() -> configurarMapa(ubicacionCliente));
                    }
                }).start();
            }
        }

        // Configuración del mapa
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapLibreMap mapLibreMapReady) {
                mapLibreMap = mapLibreMapReady;

                // Si ya hay coordenadas antes de que cargue el mapa
                if (ubicacionCliente != null) {
                    configurarMapa(ubicacionCliente);
                }
            }
        });

        // Botón para abrir Google Maps
        btnVerRuta.setOnClickListener(v -> {
            if (ubicacionCliente != null) {
                String uri = "geo:" + ubicacionCliente.getLatitude() + "," + ubicacionCliente.getLongitude()
                        + "?q=" + ubicacionCliente.getLatitude() + "," + ubicacionCliente.getLongitude()
                        + "(Destino del pedido)";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Google Maps no está instalado.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(),
                        "No se encontró la ubicación del cliente. Verifica la dirección.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Botones de chat
        btnChatCliente.setOnClickListener(v -> abrirChat("cliente"));
        btnChatTienda.setOnClickListener(v -> abrirChat("tienda"));

        return view;
    }

    private void configurarMapa(LatLng ubicacion) {
        if (mapLibreMap == null) return;

        mapLibreMap.setCameraPosition(new CameraPosition.Builder()
                .target(ubicacion)
                .zoom(15)
                .build());

        mapLibreMap.setStyle("https://tiles.openfreemap.org/styles/liberty", style -> {
            GeoJsonSource source = new GeoJsonSource("marker-source",
                    Feature.fromGeometry(Point.fromLngLat(
                            ubicacion.getLongitude(),
                            ubicacion.getLatitude())));
            style.addSource(source);

            style.addImage("marker-icon",
                    requireContext().getDrawable(R.drawable.icon_gps));

            SymbolLayer markerLayer = new SymbolLayer("marker-layer", "marker-source")
                    .withProperties(iconImage("marker-icon"));
            style.addLayer(markerLayer);

            mapLibreMap.animateCamera(
                    org.maplibre.android.camera.CameraUpdateFactory.newLatLngZoom(ubicacion, 16), 2000);
        });
    }

    private void abrirChat(String tipoDestino) {
        Bundle args = new Bundle();
        args.putString("tipoChat", tipoDestino);
        args.putSerializable("pedido", pedido);

        DeliveryMensajeriaFragment fragment = new DeliveryMensajeriaFragment();
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private LatLng obtenerCoordenadasDesdeDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) return null;

        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> direcciones = geocoder.getFromLocationName(direccion, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address address = direcciones.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Ciclo de vida del mapa
    @Override public void onStart() { super.onStart(); mapView.onStart(); }
    @Override public void onResume() { super.onResume(); mapView.onResume(); }
    @Override public void onPause() { super.onPause(); mapView.onPause(); }
    @Override public void onStop() { super.onStop(); mapView.onStop(); }
    @Override public void onDestroyView() { super.onDestroyView(); mapView.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
}
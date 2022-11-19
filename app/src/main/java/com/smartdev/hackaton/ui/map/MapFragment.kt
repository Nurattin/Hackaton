package com.smartdev.hackaton.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.smartdev.hackaton.R
import com.smartdev.hackaton.data.model.TourDetail
import com.smartdev.hackaton.data.network_layer.Api
import com.smartdev.hackaton.databinding.FragmentMapBinding
import com.smartdev.hackaton.util.CustomPointer
import com.smartdev.hackaton.util.gone
import com.smartdev.hackaton.util.visible
import com.yandex.mapkit.Animation
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.Error
import com.yandex.runtime.ui_view.ViewProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

@AndroidEntryPoint
class MapFragment : Fragment(), DrivingSession.DrivingRouteListener {

    @Inject
    lateinit var api: Api

    val viewModel: MapViewModel by viewModels()
    private val listPointer = HashMap<Int, PlacemarkMapObject>()
    private var lastPosition = 1
    private var routes = listOf<RequestPoint>()
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null
    private val args: MapFragmentArgs by navArgs()
    private val listCustomPointer = ArrayList<CustomPointer>()
    private var arrayPointer = hashMapOf<Int, Point>()
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapObjects: MapObjectCollection


    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        viewModel.getDetailTour(args.tourId)

        binding = FragmentMapBinding.bind(view).apply {
            progressBar.setContent {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 1.dp)
                }
            }

            error.setContent {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Text(text = "Ошибка")
                        Button(onClick = { viewModel.getDetailTour(args.tourId) }) {
                            Text(text = "Обновить")
                        }
                    }
                }
            }
            topBarCompose.setContent {

                Column() {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 20.dp)
                            .background(Color.White)
                    ) {
                        Text(
                            text = "Карты",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(vertical = 13.dp)
                        )
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 16.dp)
                                .clickable {
                                    findNavController().navigateUp()
                                },
                            painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_left),
                            contentDescription = null
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(start = 4.dp, top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(count = 4) {
                            Chip(
                                onClick = {},
                                colors = ChipDefaults.chipColors(
                                    contentColor = Color.Black,
                                    backgroundColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        vertical = 6.dp,
                                        horizontal = 12.dp
                                    ),
                                    text = "Отдых"
                                )
                            }
                        }
                    }
                }
            }
        }

        BottomSheetBehavior.from(binding.bottomSheetContainer).apply {
            peekHeight = 200

            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        mapObjects = binding.mapview.map.mapObjects.addCollection();
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        lifecycleScope.launchWhenStarted {
            viewModel.places.collect {
                when (val result = it) {
                    is MapUiState.Error -> {
                        binding.progressBar.gone()
                        binding.mapview.gone()
                        binding.error.visible()
                    }
                    is MapUiState.Success -> {
                        binding.progressBar.gone()
                        binding.mapview.visible()
                        binding.error.gone()
                        result.places?.let { it ->
                            setupMap(it.data.places)
                            submitRequest(it.data.places)
                            initMarkPlaces(it.data.places)

                            moveMapByPosition(0, it.data.places)
                            binding.mapview.map.mapObjects.addTapListener { mapObject, point ->
                                moveMapByPointer(point)
                                true
                            }
                        }
                    }
                    MapUiState.Loading -> {
                        binding.progressBar.visible()
                        binding.mapview.gone()
                        binding.error.gone()
                    }
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun moveMapByPointer(point: Point) {
        var cameraPosition = binding.mapview.map.cameraPosition(
            getBoundingBoxThenCameraMove(
                point.latitude,
                point.longitude,
                5
            )
        )
        cameraPosition = CameraPosition(
            point,
            cameraPosition.zoom - 1,
            cameraPosition.azimuth,
            cameraPosition.tilt
        )
        binding.mapview.map.move(cameraPosition, Animation(Animation.Type.SMOOTH, 1f), null)
    }

    private fun moveMapByPosition(pos: Int, places: List<TourDetail.Data.Place>) {
        val boundingBox = getBoundingBoxThenCameraMove(
            places[pos].geo.latitude,
            places[pos].geo.longitude,
            5
        )
        var cameraPosition = binding.mapview.map.cameraPosition(boundingBox)
        cameraPosition = CameraPosition(
            Point(places[pos].geo.latitude, places[pos].geo.longitude),
            cameraPosition.zoom - 1,
            cameraPosition.azimuth,
            cameraPosition.tilt
        )
        binding.mapview.map.move(cameraPosition, Animation(Animation.Type.SMOOTH, 1f), null)
    }

    private fun changePointer(pos: Int, places: List<TourDetail.Data.Place>) {
        val lastPointer = CustomPointer(requireContext())
        lastPointer.setValues(lastPosition, false)
        listPointer[lastPosition]?.setView(ViewProvider(lastPointer))
        lastPosition = pos
        val pointer = CustomPointer(requireContext())
        pointer.setValues(pos, true)
        listPointer[pos]?.setView(ViewProvider(pointer))
        moveMapByPosition(pos - 1, places)
    }

    private fun setupMap(places: List<TourDetail.Data.Place>) {
        binding.run {
            var cameraPosition = mapview.map.cameraPosition(getBoundingBox(places))
            cameraPosition = CameraPosition(
                cameraPosition.target,
                cameraPosition.zoom - 1,
                cameraPosition.azimuth,
                cameraPosition.tilt
            )
            mapview.map.move(cameraPosition)
        }
    }

    private fun initMarkPlaces(places: List<TourDetail.Data.Place>) {
        binding.run {
            places.forEachIndexed { index, place ->
                arrayPointer[index + 1] =
                    (Point(place.geo.latitude, place.geo.longitude))
                val pointer = CustomPointer(requireContext())
                mapObjects.userData = index
                if (listCustomPointer.count() - 1 < index) {
                    listCustomPointer.add(pointer)
                }
                pointer.setValues(place = index + 1, index + 1 == lastPosition)
                val viewProvider = ViewProvider(pointer)
                listPointer[index + 1] =
                    mapObjects.addPlacemark(
                        Point(
                            place.geo.latitude,
                            place.geo.longitude
                        ), viewProvider
                    )
            }
        }
    }

    private fun submitRequest(places: List<TourDetail.Data.Place>) {
        routes = places.map {
            RequestPoint(
                Point(it.geo.latitude, it.geo.longitude),
                RequestPointType.WAYPOINT,
                null
            )
        }
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()
        drivingSession = drivingRouter?.requestRoutes(routes, drivingOptions, vehicleOptions, this);
    }


    private fun getBoundingBoxThenCameraMove(lat: Double, lng: Double, zoom: Int = 1): BoundingBox {
        val north = lat + 0.1 * zoom
        val east = lng - 0.1 * zoom
        val west = lng + 0.1 * zoom
        val south = lat - 0.1 * zoom
        return BoundingBox(Point(north, east), Point(south, west))
    }

    private fun getBoundingBox(places: List<TourDetail.Data.Place>): BoundingBox {
        var lat: Double
        var lng: Double
        var north = places[0].geo.latitude // север
        var west = places[0].geo.longitude // запад
        var east = places[0].geo.longitude // восток
        var south = places[0].geo.latitude // юг
        places.forEach {
            lat = it.geo.latitude
            lng = it.geo.longitude
            north = if (north < lat) lat else north
            west = if (west < lng) lng else west
            east = if (east > lng) lng else east
            south = if (south > lat) lat else south
        }
        return BoundingBox(Point(north, east), Point(south, west))
    }

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        p0.forEach { mapObjects.addPolyline(it.geometry) }
    }

    override fun onDrivingRoutesError(p0: Error) {
        Toast.makeText(requireContext(), p0.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        binding.run {
            mapview.onStop()
        }
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        binding.run {
            mapview.onStart()
        }
    }

    private fun stopProgressBar() {
        Timer().schedule(500) {
            activity?.runOnUiThread {
                with(binding) {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
}

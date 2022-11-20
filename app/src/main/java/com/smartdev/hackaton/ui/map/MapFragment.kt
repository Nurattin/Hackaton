package com.smartdev.hackaton.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
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
import javax.inject.Inject

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


    @OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        viewModel.getDetailTour(args.tourId)

        binding = FragmentMapBinding.bind(view).apply {
            progressBar.setContent {


                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
                val chips = viewModel.chips.collectAsStateWithLifecycle()

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
                        items(items = chips.value) { item ->
                            Chip(
                                onClick = { viewModel.changeCategorySelected(item) },
                                colors = ChipDefaults.chipColors(
                                    contentColor = if (item.isSelected) Color.White else Color.Black,
                                    backgroundColor = if (item.isSelected) Color(0xFF76A595) else Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(Dp.Hairline, Color.Black.copy(0.1f))
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        vertical = 6.dp,
                                        horizontal = 12.dp
                                    ),
                                    text = item.name
                                )
                            }
                        }
                    }
                }
            }
            bottomSheetContent.setContent {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .padding(top = 26.dp)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Кинотеатр “Октябрь”",
                        textAlign = TextAlign.Center
                    )
                    verticalSpace(height = 16.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Рейтинг 5.0",
                            textAlign = TextAlign.Center
                        )
                    }
                    verticalSpace(height = 16.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .weight(1 / 3f),
                            model = listImage.random(),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.place_holder)
                        )
                        Column(
                            Modifier
                                .aspectRatio(1f)
                                .weight(1 / 3f),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            repeat(2) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .weight(1 / 2f),
                                        contentScale = ContentScale.Crop,
                                        model = listImage.random(),
                                        contentDescription = "",
                                        placeholder = painterResource(id = R.drawable.place_holder)
                                    )
                                    AsyncImage(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .weight(1 / 2f),
                                        contentScale = ContentScale.Crop,

                                        model = listImage.random(),
                                        contentDescription = "",
                                        placeholder = painterResource(id = R.drawable.place_holder)
                                    )
                                }
                            }
                        }
                        AsyncImage(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .weight(1 / 3f),
                            model = listImage.random(),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.place_holder)
                        )
                    }
                    verticalSpace(height = 26.dp)
                    Divider(modifier = Modifier.fillMaxWidth())
                    verticalSpace(height = 16.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(1.dp, Color.Black.copy(0.1f)),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(text = "~250р")
                        }

                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF76A595),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(text = "Добавить")
                        }
                    }
                    verticalSpace(height = 16.dp)
                    Divider(modifier = Modifier.fillMaxWidth())
                    verticalSpace(height = 26.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(text = "Отзывы", color = Color.Black.copy(0.5f))

                        Text(text = "все ->", color = Color.Black.copy(0.5f))
                    }

                    verticalSpace(height = 8.dp)
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(5) {
                            Card(
                                modifier = Modifier
                                    .height(81.dp)
                                    .width(214.dp),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.1f))
                                        .padding(6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .shadow(10.dp, shape = CircleShape),
                                        contentScale = ContentScale.Crop,
                                        model = "https://i.pinimg.com/originals/25/cf/17/25cf170c0b6463eff6ebe89983c95dbd.jpg",
                                        contentDescription = null,
                                    )
                                    Column(verticalArrangement = Arrangement.SpaceBetween) {
                                        Text(
                                            modifier = Modifier.padding(bottom = 6.dp),
                                            text = "Имя Фамилия",
                                            fontSize = 10.sp
                                        )

                                        Text(
                                            modifier = Modifier.padding(),
                                            text = "Большой зал, крутые фильмы, советую!!",
                                            color = Color.Black.copy(alpha = 0.5f),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    verticalSpace(height = 8.dp)
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = "Найти в интернете ->",
                        color = Color.Black.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                    verticalSpace(height = 32.dp)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        items(5) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                contentScale = ContentScale.Crop,
                                model = listIntresting.random(),
                                contentDescription = null
                            )
                        }
                    }
                    verticalSpace(height = 32.dp)
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

//    private fun changePointer(pos: Int, places: List<TourDetail.Data.Place>) {
//        val lastPointer = CustomPointer(requireContext())
//        lastPointer.setValues(lastPosition, false)
//        listPointer[lastPosition]?.setView(ViewProvider(lastPointer))
//        lastPosition = pos
//        val pointer = CustomPointer(requireContext())
//        listPointer[pos]?.setView(ViewProvider(pointer))
//        moveMapByPosition(pos - 1, places)
//    }

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
                pointer.setValues(
                    place = index + 1,
                    index + 1 == lastPosition,
                    category = place.categories.name
                )
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
}

private val listImage = listOf(
    "https://waydag.ru/upload/iblock/335/dgxbxeqcl10vt4rb6i74kfv907i5prmc.jpg",
    "https://i.bolshayastrana.com/1200x00/dc/af/dcaf6b197623a255a7dce378547f5b45.jpeg",
    "http://www.forumargo.ru/upload/servicephoto/photo_67751a24fbe8b508af84645b02398061.JPG",
    "https://uploads2.stells.info/storage/jpg/9/7d/97d7bfbd482ee2c94177d27eb44030bb.jpg",
    "https://static.tildacdn.com/tild3763-3764-4035-b631-396465383738/__2.jpg",
    "https://sportishka.com/uploads/posts/2022-03/thumbs/1646333830_40-sportishka-com-p-sulakskaya-laguna-turizm-krasivo-foto-49.jpg",
    "https://www.activilla.com/images/detailed/64/ekskursionnyy-tur-zavorazhivayuschiy-dagestan-62268.jpg",
    "https://s8.stc.all.kpcdn.net/russia/wp-content/uploads/2022/05/Mahachkala-kver-Stalskogo-s-vysoty.jpg",
    "https://static.orgpage.ru/socialnewsphotos/c9/c9935d28f8a74b96909d392b1a62763a.jpg",
    "https://www.riadagestan.ru/upload/iblock/769/769604e7bd83d47e9432fd1200247dbf.jpg"


)

private val listIntresting = listOf(
    "https://static.tildacdn.com/tild3038-6165-4435-b435-346638306164/Gamsol-Russian-Machu.jpg",
    "https://iertour.ru/wp-content/uploads/2021/07/kanon-1.jpg",
    "https://www.veditour.ru/upload/tours/5190/1-1.jpg",
    "https://static.tildacdn.com/tild6337-3336-4537-b832-303761633939/_3.jpg",
    "https://a.d-cd.net/VyAAAgOIMeA-1920.jpg",
    "https://vsegda-pomnim.com/uploads/posts/2022-04/1650940362_51-vsegda-pomnim-com-p-gornii-dagestan-foto-51.jpg",
    "https://static.tildacdn.com/tild3763-3764-4035-b631-396465383738/__2.jpg"
)

@Composable
fun ColumnScope.verticalSpace(height: Dp) = Spacer(modifier = Modifier.height(height))

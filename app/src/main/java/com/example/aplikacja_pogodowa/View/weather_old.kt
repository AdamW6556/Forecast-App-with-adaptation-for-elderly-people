package com.example.aplikacja_pogodowa.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.aplikacja_pogodowa.R
import kotlinx.android.synthetic.main.fragment_weather_old.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import android.widget.SearchView
import android.os.AsyncTask
import com.example.aplikacja_pogodowa.databinding.FragmentWeatherOldBinding
import android.widget.Toast


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [weather_old.newInstance] factory method to
 * create an instance of this fragment.
 */
class weather_old : Fragment() {

    private var _binding: FragmentWeatherOldBinding? = null
    private val binding get() = _binding!!
    //defined variables, api key from openweather side
    val API_KEY: String = "1327b4a698b65a5c2f181bf293b040af"
    //city variable for city to get its weather
    var city: String = ""


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //defined binding for get later elements from view
        _binding = FragmentWeatherOldBinding.inflate(inflater, container, false)
        val view = binding.root

        //citysearch2 searchview to get input of city from user with appropiate functions to show
        // full information about weather of this city
        binding.citysearch2.setOnQueryTextListener(object :SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null) {
                    city = p0
                    weatherTask().execute()
                }
                else
                {
                    Toast.makeText(requireContext(), "Empty city input!", Toast.LENGTH_SHORT).show()
                }

                return false
            }

        })

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //navigation to switch screen look to standard users screen
        zmianananormal.setOnClickListener{ view ->
            view.findNavController().navigate(R.id.action_oldFragment_to_normalFragment)
        }


    }

    //first async task function to configure first calculations in background
    // before start execution
    inner class weatherTask(): AsyncTask<String,Void,String>() {
        override fun onPreExecute() {

            super.onPreExecute()

        }

        //function to perform calculations in background, which can take some time
        override fun doInBackground(vararg p0: String?): String? {

            var response: String?
            try
            {
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API_KEY").readText(Charsets.UTF_8)
            }
            catch (e: Exception)
            {
                response = null

            }
            return response

        }



        //function to get result from calculations in background after all background work
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try{

                //defined json objects to get variables from api in json and write them to variables in kotlin

                val jsonObj=JSONObject(result)

                val main = jsonObj.getJSONObject("main")

                val sys = jsonObj.getJSONObject("sys")

                val city = jsonObj.getString("name")+","+sys.getString("country")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")

                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val weatherdescription = weather.getString("description")

                val datatime:Long = jsonObj.getLong("dt")
                val datatimetext = SimpleDateFormat("dd.MM.yyyy hh:mm a", Locale.ENGLISH).format(Date(datatime*1000))

                val temp = main.getString("temp")+"°C"

                val pressure = main.getString("pressure")+" hPa"

                val icone = weather.getString("icon")

                //get icon for weather
                Iconforweather(icone)

                //binding elements from view by binding them with variables defined above
                binding.tvcity.text = city

                binding.tvData.text = datatimetext

                binding.tvTemp.text=temp

                binding.tvfeelslike.text = weatherdescription

                binding.tvPress.text = pressure

                binding.tvSunrise.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))

                binding.tvSunset.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))

            }
            catch (e:Exception)
            {}

        }


        //function to get icon of weather by switch appropiate symbols and switch them
        // to correct image view
        private fun Iconforweather(icon: String){
            val icons = icon
                    .replace("n","")
                    .replace("d","")

            when(icons){
                "01" -> binding.imageView.setImageResource(R.drawable.n_clear_sky)

                "02" -> binding.imageView.setImageResource(R.drawable.n_few_clouds)

                "03" -> binding.imageView.setImageResource(R.drawable.n_scattered_clouds)

                "04" -> binding.imageView.setImageResource(R.drawable.n_broken_clouds)

                "09" -> binding.imageView.setImageResource(R.drawable.n_shower_rain)

                "10" -> binding.imageView.setImageResource(R.drawable.n_rain)

                "11" -> binding.imageView.setImageResource(R.drawable.n_thunderstorm)

                "13" -> binding.imageView.setImageResource(R.drawable.n_snow)

                "50" -> binding.imageView.setImageResource(R.drawable.n_mist)
            }
        }
    }

}
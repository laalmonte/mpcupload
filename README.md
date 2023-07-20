# mpcupload

This is mpcupload app as for Martin Pulgar Construction uploading app.
This is done using the following technologies:
- MVVM Design Pattern
- Hilt Dependency Injection
- Retrofit
- Camera API
- Camera2 API
- Gallery and other Intents
- RecyclerView/Adapter/ViewHolder
- Datepicker/ DateListener
- ActivityResultLaunchers
- Data Classes/ Serializable
- Picasso, Bitmap handling, Uri handling
- Spinners
- LiveData, Lifecycle Observables
- ViewModelScope - Coroutine
- API REST interface
- PermissionHandling
- ViewBinding
- More on activities rather then fragmented

Scope:
- It can capture images using Camera API, then store in recycler to preview.
- It can also get from gallery if need, just commenting out some code.
- It can call post api for the fake API upload scenario
- It can go to camera2 API page
- It can call get api if needed
- It can handle dates, spinner selections and input texts.
- It has a simple validation shown by toast
- It has a good reactive layout

Limitations:
- camera2 API has not been tested properly due to my personal device not having camera from that list
- photos are not reflected in api response due to list issues so it will just show null for now
- camera API is working but has deprecated result handling .getData()
- only a few inputs are incorporated in the validation and no custom dialog just toast
- checkboxes are not yet included functionality

Schedules of per technology:
yesterday:
UI, adapters, cameraAPI, initial dependency injection, viewmodel, and other ui behaviour
today: 
camera2 api, retrofit get and post, quick validation and other finishing details
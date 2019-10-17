package me.betterclever.hospinav.utils

data class DepartmentInfo (
        val department: Department,
        val name: String,
        val description: String,
        val openingTime: Long,
        val closingTime: Long,
        val primaryDoctorName: String? = null
)

enum class Department {
    Entrance,
    Storage,
    PathologyDepartment,
    MRILab,
    Washroom,
    ICU,
    XRayRoom,
    OrthopaedicDepartment,
    BloodBank,
    Bath,
    Reception;

    private val departmentName
        get() = when (this) {
            Entrance -> "Entrance"
            Storage -> "Storage"
            PathologyDepartment -> "Pathology Department"
            MRILab -> "MRI Lab"
            Washroom -> "Washroom"
            ICU -> "Intensive Care Unit"
            XRayRoom -> "X-Ray Room"
            OrthopaedicDepartment -> "Orthopaedic Department"
            BloodBank -> "Blood Bank"
            Bath -> "Bath"
            Reception -> "Reception"
        }

    val departmentInfo
        get() = when (this) {
            Entrance -> DepartmentInfo(
                    this,
                    this.departmentName,
                    openingTime = 900,
                    closingTime = 1700,
                    description = "This is where you enter"
            )
            Storage -> DepartmentInfo(
                    this,
                    this.departmentName,
                    openingTime = 900,
                    closingTime = 1700,
                    description = ""
            )
            PathologyDepartment -> DepartmentInfo(
                    this,
                    this.departmentName,
                    openingTime = 900,
                    closingTime = 1700,
                    primaryDoctorName = "Dr. Santosh Kumar",
                    description = ""
            )
            MRILab -> DepartmentInfo(
                    this,
                    this.departmentName,
                    openingTime = 900,
                    closingTime = 1700,
                    primaryDoctorName = "Dr. Arubha Srivastava",
                    description = "An MRI or magnetic resonance imaging is a radiology techinque scan that uses magnetism, radio waves, and a computer to produce images of body structures. The MRI scanner is a tube surrounded by a giant circular magnet. The patient is placed on a moveable bed that is inserted into the magnet. The magnet creates a strong magnetic field that aligns the protons of hydrogen atoms, which are then exposed to a beam of radio waves. This spins the various protons of the body, and they produce a faint signal that is detected by the receiver portion of the MRI scanner. A computer processes the receiver information, which produces an image."
            )
            Washroom -> DepartmentInfo(
                    this,
                    this.departmentName,
                    openingTime = 900,
                    closingTime = 1700,
                    description = ""
            )
            ICU -> DepartmentInfo(
                    this,
                    this.departmentName,
                    openingTime = 900,
                    closingTime = 1700,
                    description = ""
            )
            XRayRoom -> DepartmentInfo(
                    this,
                    this.departmentName,
                    openingTime = 900,
                    closingTime = 1700,
                    primaryDoctorName = "Dr. Harsh Sharma",
                    description = "X-rays make up X-radiation, a form of electromagnetic radiation. Most X-rays have a wavelength ranging from 0.01 to 10 nanometers, corresponding to frequencies in the range 30 petahertz to 30 exahertz (3×1016 Hz to 3×1019 Hz) and energies in the range 100 eV to 100 keV. X-ray wavelengths are shorter than those of UV rays and typically longer than those of gamma rays."
            )
            OrthopaedicDepartment -> DepartmentInfo(
                    this,
                    this.departmentName,
                    openingTime = 900,
                    closingTime = 1700,
                    primaryDoctorName = "Dr. Kunal Singh",
                    description = ""
            )
            BloodBank -> DepartmentInfo(
                    this,
                    this.departmentName,
                    openingTime = 900,
                    closingTime = 1700,
                    primaryDoctorName = "Dr. Sunil Shah",
                    description = "A blood bank is a center where blood gathered as a result of blood donation is stored and preserved for later use in blood transfusion.\n\nBlood Packets Available: A+-4,B+-3"
            )
            Bath -> DepartmentInfo(
                    this,
                    this.departmentName,
                    openingTime = 900,
                    closingTime = 1700,
                    description = ""
            )
            Reception -> DepartmentInfo(
                    this,
                    this.departmentName,
                    openingTime = 900,
                    closingTime = 1700,
                    description = ""
            )
        }
}

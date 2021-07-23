int main() {
    Print("true:\t", "salam" == "sal" + "am");
    Print("true:\t", "salam" == "salam");
    Print("$$$$", "sal" + "am", "$$$$");
    Print("$$$$", "salam", "$$$$");
    Print("false:\t", "salam" != "sal" + "am");
    Print("false:\t", "salnm" == "sal" + "am");
    Print("true:\t", "salnm" != "sal" + "am");
}
package rs.mozaikmozgalica.app.data


object QuizContent {

    val koZnaZnaQuestions: List<TriviaQuestion> = emptyList()

    val kolikoZnasSrbijuQuestions: List<TriviaQuestion> = listOf(
        TriviaQuestion(
            id = 101,
            category = "Manastiri",
            question = "Čija je zadužbina manastir Studenica?",
            options = listOf("Stefana Nemanje", "Kralja Milutina", "Svetog Save", "Kneza Lazara"),
            correctIndex = 0,
            funFact = "Manastir Studenica je podignut krajem 12. veka i upisan je na Uneskovu listu svetske baštine."
        ),
        TriviaQuestion(
            id = 102,
            category = "Geografija",
            question = "Koji je najviši vrh planine Kopaonik?",
            options = listOf("Midžor", "Pančićev vrh", "Pogled", "Šiljak"),
            correctIndex = 1,
            funFact = "Vrh je dobio ime po čuvenom botaničaru Josifu Pančiću, čiji se mauzolej nalazi upravo na tom mestu."
        ),
        TriviaQuestion(
            id = 103,
            category = "Istorija",
            question = "Koje godine se dogodio Prvi srpski ustanak?",
            options = listOf("1804.", "1815.", "1878.", "1912."),
            correctIndex = 0,
            funFact = "Ustanak je počeo zborom u Orašcu na Sretenje, a za vođu je izabran Đorđe Petrović, poznatiji kao Karađorđe."
        ),
        TriviaQuestion(
            id = 104,
            category = "Hrana",
            question = "Iz kog kraja Srbije potiče čuvena peglana kobasica?",
            options = listOf("Zlatibor", "Sjenica", "Pirot", "Srem"),
            correctIndex = 2,
            funFact = "Pravi se od kvalitetnog mesa (najčešće goveđeg i ovčijeg) uz puno belog luka i tucane paprike, a pegla se flašama kako bi se istisnuo vazduh."
        ),
        TriviaQuestion(
            id = 105,
            category = "Geografija",
            question = "Koje dve reke se spajaju kod Beograda?",
            options = listOf("Sava i Tisa", "Sava i Dunav", "Drina i Sava", "Morava i Dunav"),
            correctIndex = 1,
            funFact = "Ušće Save u Dunav, posmatrano sa Kalemegdanske tvrđave, predstavlja jedno od najlepših rečnih ušća u Evropi."
        ),
        TriviaQuestion(
            id = 106,
            category = "Nacionalni parkovi",
            question = "Koji od ovih predela NIJE nacionalni park u Srbiji?",
            options = listOf("Tara", "Fruška gora", "Zlatibor", "Đerdap"),
            correctIndex = 2,
            funFact = "Srbija ima pet nacionalnih parkova: Fruška gora, Đerdap, Tara, Kopaonik i Šar planina. Zlatibor je park prirode, ali ne i nacionalni park."
        ),
        TriviaQuestion(
            id = 107,
            category = "Istorijske ličnosti",
            question = "Koji naš naučnik se smatra ocem naizmenične struje?",
            options = listOf("Mihajlo Pupin", "Milutin Milanković", "Nikola Tesla", "Josif Pančić"),
            correctIndex = 2,
            funFact = "Tesla je rođen u Smiljanu u Lici, a tokom života prijavio je stotine patenata koji su promenili moderni svet."
        ),
        TriviaQuestion(
            id = 108,
            category = "Običaji",
            question = "Kako se zove pogača koja se lomi na Božić, a u koju se stavlja novčić?",
            options = listOf("Slavski kolač", "Česnica", "Kravaj", "Lepinja"),
            correctIndex = 1,
            funFact = "Veruje se da će član porodice koji pronađe novčić u svom delu česnice imati sreće i novca tokom cele godine."
        ),
        TriviaQuestion(
            id = 109,
            category = "Gradovi",
            question = "Koji grad se ranije zvao Singidunum?",
            options = listOf("Novi Sad", "Niš", "Beograd", "Kragujevac"),
            correctIndex = 2,
            funFact = "Ime Singidunum su gradu dali Kelti, a kasnije su ga utvrdili i naselili Rimljani."
        ),
        TriviaQuestion(
            id = 110,
            category = "Manastiri",
            question = "U kom manastiru se nalaze mošti kneza Lazara?",
            options = listOf("Ravanica", "Žiča", "Ljubostinja", "Krušedol"),
            correctIndex = 0,
            funFact = "Manastir Ravanica, podignut u moravskom stilu, zadužbina je samog kneza Lazara Hrebeljanovića."
        ),
        TriviaQuestion(
            id = 111,
            category = "Geografija",
            question = "Koja reka predstavlja najveći deo prirodne granice između Srbije i Bosne i Hercegovine?",
            options = listOf("Sava", "Lim", "Dunav", "Drina"),
            correctIndex = 3,
            funFact = "Drina je čuvena po svojoj zelenoj boji i brzacima, a nastaje spajanjem Tare i Pive u Crnoj Gori."
        ),
        TriviaQuestion(
            id = 112,
            category = "Hrana",
            question = "Kako se zove specijalitet, vrsta kajmaka koji zri u životinjskoj mešini, specifičan za zlatiborski i sjenički kraj?",
            options = listOf("Urnebes", "Skorup iz mješine (ili mešina kajmak)", "Mladi sir", "Caciki"),
            correctIndex = 1,
            funFact = "Čuvanje u mešini daje ovom kajmaku (skorupu) poseban, jak ukus i miris, pa se smatra vrhunskim delikatesom."
        ),
        TriviaQuestion(
            id = 113,
            category = "Istorija",
            question = "Koji rimski car, inače rođen na teritoriji današnje Srbije, je doneo Milanski edikt?",
            options = listOf("Galerije", "Dioklecijan", "Konstantin Veliki", "Trajan"),
            correctIndex = 2,
            funFact = "Konstantin Veliki, rođen u Naissusu (današnji Niš), Milanskim ediktom 313. godine je proglasio versku toleranciju."
        ),
        TriviaQuestion(
            id = 114,
            category = "Običaji",
            question = "Kada se proslavlja pravoslavna Nova godina (po gregorijanskom kalendaru)?",
            options = listOf("1. januara", "7. januara", "13/14. januara", "19. januara"),
            correctIndex = 2,
            funFact = "Pravoslavna, odnosno Srpska Nova godina se slavi u noći između 13. i 14. januara, po Julijanskom kalendaru to je 1. januar."
        ),
        TriviaQuestion(
            id = 115,
            category = "Manastiri",
            question = "Koji srpski manastir krasi čuvena freska 'Beli anđeo'?",
            options = listOf("Sopoćani", "Mileševa", "Pećka patrijaršija", "Dečani"),
            correctIndex = 1,
            funFact = "Beli anđeo je zapravo deo veće kompozicije Mironosice na Hristovom grobu, i smatra se jednim od najlepših dela srpske srednjovekovne umetnosti."
        ),
        TriviaQuestion(
            id = 116,
            category = "Geografija",
            question = "Koje je najveće jezero u Srbiji, a da je pritom veštačko?",
            options = listOf("Vlasinsko jezero", "Palićko jezero", "Borsko jezero", "Đerdapsko jezero"),
            correctIndex = 3,
            funFact = "Đerdapsko jezero je nastalo pregrađivanjem reke Dunav zbog izgradnje hidroelektrane Đerdap 1."
        ),
        TriviaQuestion(
            id = 117,
            category = "Gradovi",
            question = "U kom gradu se održava čuveni Sabor trubača?",
            options = listOf("Vranju", "Užicu", "Guči", "Leskovcu"),
            correctIndex = 2,
            funFact = "Sabor u Guči (Dragačevski sabor) održava se svake godine još od 1961. godine i privlači hiljade turista."
        ),
        TriviaQuestion(
            id = 118,
            category = "Istorijske ličnosti",
            question = "Ko je Vuk Stefanović Karadžić?",
            options = listOf("Prvi srpski arhiepiskop", "Reformator srpskog jezika i pravopisa", "Vođa Prvog srpskog ustanka", "Kralj Srbije"),
            correctIndex = 1,
            funFact = "Vuk Karadžić je autor načela 'Piši kao što govoriš, a čitaj kao što je napisano', koje je uprostilo učenje čitanja i pisanja."
        ),
        TriviaQuestion(
            id = 119,
            category = "Priroda",
            question = "Koji prirodni fenomen, specifične zemljane figure kod Kuršumlije, je bio kandidat za Sedam svetskih čuda prirode?",
            options = listOf("Rajkova pećina", "Đavolja varoš", "Resavska pećina", "Lazarev kanjon"),
            correctIndex = 1,
            funFact = "Đavolju varoš čine preko 200 zemljanih piramida (kula) na čijim vrhovima se nalaze kameni blokovi, andezitske kape."
        ),
        TriviaQuestion(
            id = 120,
            category = "Manastiri",
            question = "Manastir koji je bio sedište prve srpske arhiepiskopije i mesto krunisanja vladara iz loze Nemanjića je:",
            options = listOf("Žiča", "Studenica", "Gračanica", "Sopoćani"),
            correctIndex = 0,
            funFact = "Žiču su podigli kralj Stefan Prvovenčani i njegov brat Sveti Sava. Prepoznatljiva je po crvenoj boji fasade."
        ),
        TriviaQuestion(
            id = 121,
            category = "Hrana",
            question = "Koje je tradicionalno piće koje se peče od šljive u Srbiji?",
            options = listOf("Lozovača", "Klekovača", "Šljivovica", "Viljamovka"),
            correctIndex = 2,
            funFact = "Srpska šljivovica (znanje i veština pečenja) je 2022. godine upisana na Uneskovu reprezentativnu listu nematerijalnog kulturnog nasleđa čovečanstva."
        ),
        TriviaQuestion(
            id = 122,
            category = "Geografija",
            question = "Koji grad u Srbiji leži na pet reka (Đetinja, Skrapež, Moravica, Zapadna Morava i Lužnica posredno/neposredno u okolini) - odnosno poznat je po Đetinji?",
            options = listOf("Čačak", "Užice", "Kraljevo", "Valjevo"),
            correctIndex = 1,
            funFact = "Na reci Đetinji u Užicu izgrađena je 1900. godine prva hidroelektrana u Evropi po Teslinim principima polifazne struje."
        ),
        TriviaQuestion(
            id = 123,
            category = "Istorijske ličnosti",
            question = "Ko je jedini srpski nobelovac (za književnost)?",
            options = listOf("Miloš Crnjanski", "Desanka Maksimović", "Branislav Nušić", "Ivo Andrić"),
            correctIndex = 3,
            funFact = "Deo novčane nagrade od Nobela Ivo Andrić je poklonio za unapređenje bibliotekarstva u Bosni i Hercegovini."
        ),
        TriviaQuestion(
            id = 124,
            category = "Običaji",
            question = "Kako se zove običaj obeležavanja sveca zaštitnika porodice, koji je specifičan prvenstveno za Srbe?",
            options = listOf("Slava (Krsna slava)", "Zaveatina", "Preslava", "Litija"),
            correctIndex = 0,
            funFact = "Krsna slava je još jedan od srpskih običaja koji se našao na Uneskovoj listi nematerijalnog kulturnog nasleđa čovečanstva."
        ),
        TriviaQuestion(
            id = 125,
            category = "Gradovi",
            question = "Koji se naš grad nalazi na obalama Nišave i poznat je po Ćele-kuli?",
            options = listOf("Pirot", "Vranje", "Niš", "Leskovac"),
            correctIndex = 2,
            funFact = "Ćele-kulu su izgradili Turci od lobanja srpskih vojnika poginulih u bici na Čegru 1809. godine, pod komandom Stevana Sinđelića."
        )
    )

    val spojnicePool: List<SpojniceRound> = listOf(
        SpojniceRound(
            id = 1,
            title = "Pisci i poznata dela",
            pairs = listOf(
                SpojnicePair("Ivo Andrić", "Na Drini ćuprija"),
                SpojnicePair("Bora Stanković", "Nečista krv"),
                SpojnicePair("Miloš Crnjanski", "Seobe"),
                SpojnicePair("Branislav Nušić", "Sumnjivo lice"),
                SpojnicePair("Dobrica Ćosić", "Koreni")
            )
        ),
        SpojniceRound(
            id = 2,
            title = "Gradovi i reke Srbije",
            pairs = listOf(
                SpojnicePair("Novi Sad", "Dunav"),
                SpojnicePair("Niš", "Nišava"),
                SpojnicePair("Užice", "Đetinja"),
                SpojnicePair("Valjevo", "Kolubara"),
                SpojnicePair("Kraljevo", "Ibar")
            )
        ),
        SpojniceRound(
            id = 3,
            title = "Države i glavni gradovi (Evropa)",
            pairs = listOf(
                SpojnicePair("Italija", "Rim"),
                SpojnicePair("Španija", "Madrid"),
                SpojnicePair("Austrija", "Beč"),
                SpojnicePair("Mađarska", "Budimpešta"),
                SpojnicePair("Grčka", "Atina")
            )
        ),
        SpojniceRound(
            id = 4,
            title = "Glumci i čuvene uloge (filmovi)",
            pairs = listOf(
                SpojnicePair("Bata Stojković", "Ilija Čvorović"),
                SpojnicePair("Dragan Nikolić", "Flojd"),
                SpojnicePair("Ljubiša Samardžić", "Šurda"),
                SpojnicePair("Pavle Vuisić", "Paja Čutura"),
                SpojnicePair("Bora Todorović", "Đenka")
            )
        ),
        SpojniceRound(
            id = 5,
            title = "Zadužbine i vladari",
            pairs = listOf(
                SpojnicePair("Stefan Nemanja", "Studenica"),
                SpojnicePair("Knez Lazar", "Ravanica"),
                SpojnicePair("Kralj Milutin", "Gračanica"),
                SpojnicePair("Stefan Dečanski", "Visoki Dečani"),
                SpojnicePair("Despot Stefan", "Manasija")
            )
        ),
        SpojniceRound(
            id = 6,
            title = "Tradicionalna hrana i krajevi",
            pairs = listOf(
                SpojnicePair("Peglana kobasica", "Pirot"),
                SpojnicePair("Komplet lepinja", "Užice"),
                SpojnicePair("Futoški kupus", "Bačka"),
                SpojnicePair("Roštilj", "Leskovac"),
                SpojnicePair("Kajmak i pršuta", "Zlatibor")
            )
        ),
        SpojniceRound(
            id = 7,
            title = "Poznati bendovi i pesme (YU rok)",
            pairs = listOf(
                SpojnicePair("Riblja Čorba", "Lutka sa naslovne strane"),
                SpojnicePair("Bijelo Dugme", "Lipe cvatu"),
                SpojnicePair("Bajaga i Instruktori", "Plavi safir"),
                SpojnicePair("Idoli", "Maljčiki"),
                SpojnicePair("EKV", "Krug")
            )
        ),
        SpojniceRound(
            id = 8,
            title = "Dopuni poslovicu",
            pairs = listOf(
                SpojnicePair("Ko rano rani,", "dve sreće grabi."),
                SpojnicePair("Ispeci,", "pa reci."),
                SpojnicePair("Tiha voda,", "breg roni."),
                SpojnicePair("Bolje vrabac u ruci,", "nego golub na grani."),
                SpojnicePair("Gvožđe se kuje,", "dok je vruće.")
            )
        ),
        SpojniceRound(
            id = 9,
            title = "Kompozitori i dela",
            pairs = listOf(
                SpojnicePair("V.A. Mocart", "Mala noćna muzika"),
                SpojnicePair("L.V. Betoven", "Oda radosti"),
                SpojnicePair("Petar Iljič Čajkovski", "Labudovo jezero"),
                SpojnicePair("Stevan Stojanović Mokranjac", "Rukoveti"),
                SpojnicePair("Đuzepe Verdi", "Aida")
            )
        ),
        SpojniceRound(
            id = 10,
            title = "Sportisti i sportovi",
            pairs = listOf(
                SpojnicePair("Novak Đoković", "Tenis"),
                SpojnicePair("Vlade Divac", "Košarka"),
                SpojnicePair("Dragan Džajić", "Fudbal"),
                SpojnicePair("Slobodan Živojinović", "Tenis"),
                SpojnicePair("Vanja Grbić", "Odbojka")
            )
        ),
        SpojniceRound(
            id = 11,
            title = "Istorijski događaji i godine",
            pairs = listOf(
                SpojnicePair("Kosovska bitka", "1389."),
                SpojnicePair("Prvi srpski ustanak", "1804."),
                SpojnicePair("Drugi srpski ustanak", "1815."),
                SpojnicePair("Završetak I svetskog rata", "1918."),
                SpojnicePair("Otkriće Amerike", "1492.")
            )
        ),
        SpojniceRound(
            id = 12,
            title = "Tradicionalni zanati (šta ko radi)",
            pairs = listOf(
                SpojnicePair("Kovač", "Kuje gvožđe"),
                SpojnicePair("Abadžija", "Pravi suknena odela"),
                SpojnicePair("Opančar", "Pravi obuću (opanke)"),
                SpojnicePair("Mutavdžija", "Tka vreće i ponjave"),
                SpojnicePair("Licidar", "Pravi srca i kolačiće")
            )
        )
    )
}

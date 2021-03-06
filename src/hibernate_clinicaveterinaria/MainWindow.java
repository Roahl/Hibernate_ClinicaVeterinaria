package hibernate_clinicaveterinaria;
import POJOS.*;
import funciones.*;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;




public class MainWindow extends javax.swing.JFrame {

    Timer timer;
    boolean demo;
    public static int modo;
    static DefaultTableModel modeloCitas;
    static DefaultListModel modeloListaCitas;
    static DefaultListModel modeloListaMedicamentosBD;
    static DefaultListModel modeloListaMedicamentos;
    DefaultTableModel modeloVet;
    static DefaultTableModel modeloClientes;
    static Object[][] citas = new Object[38][8];
    static String[] horas = {"9:00","9:15","9:30","9:45","10:00","10:15","10:30","10:45",
                              "11:00","11:15","11:30","11:45","12:00","12:15","12:30","12:45",
                              "13:00","13:15","13:30","13:45","     ",
                              "17:00","17:15","17:30","17:45","18:00","18:15","18:30","18:45",
                              "19:00","19:15","19:30","19:45","20:00","20:15","20:30","20:45"};
    static String[] tipos = {"Gato","Perro","Pajaro","Pez","Roedor","Vaca","Cerdo","Caballo"};
    static String[] razasPerro = {"Carlino","Akita","Beagle"};
    static String[] vacunasPerro = {"Parvo","Moquillo","Parainfluenza","Rabia"};
    static String[] razasGato = {"Siames","Persa","Bengala"};
    static String[] vacunasGato = {"Tri_Virica","Panleucopenia","Rinotraqueitis","Calcivirosis","Leucemia felina","Clamidias"};
    static String[] razasPajaro = {"Periquito","Agaporni","Loro"};
    static String[] vacunasPajaro = {"Viruela","Profilaxis","Bronipra","Coripravac"};
    static String[] razasPez= {"Ángel","Gato","Arcoiris"};
    static String[] vacunasPez = {"Intra-peritoneal","Anemia","Viremia","Septicemia"};
    static String[] razasRoedor = {"Cobaya","Hamster","Jerbo"};
    static String[] vacunasRoedor = {"Mixomatosis","Hemorragia virica","Nobivac","Rabia"};
    static String[] razasVaca = {"Holstein","Shorthorn","Hereford"};
    static String[] vacunasVaca = {"Fiebre aftosa","Rabia","Edema maligno","Virus-T"};
    static String[] razasCerdo = {"Vietnamita","Duroc","Berkshire"};
    static String[] vacunasCerdo = {"Pasyeurela","Salmonela","Actinobacillus","Ecoli"};
    static String[] razasCaballo = {"Pura Sangre","Cuarto de Milla","Mustang"};
    static String[] vacunasCaballo = {"Influenza","Tetanos","Rinoneumonitis","Papera equina"};
    boolean update;
    int idUpdate;
    static Date fechaInicio, fechaFin;
    
    
    public MainWindow() {
        
        initComponents();
        
        timer = new Timer(50, new ActionListener() {
            int counter = 10;
            public void actionPerformed(ActionEvent ae) {
                counter++;
                pbLoading.setValue(counter);
                if (pbLoading.getValue()==100) {
                    timer.stop();
                    MainWindow.this.setVisible(false);
                    frameClientes.setSize(950, 750);
                    frameClientes.setLocationRelativeTo(null);
                    frameClientes.setVisible(true);
                } 
            }
        });
        
        
        panelClientes.setVisible(true);
        panelCitas.setVisible(false);
        panelVeterinarios.setVisible(false);
        panelFacturas.setVisible(false);
        panelInventario.setVisible(false);
        
        
        
        String[] columnasClientes={"id", "Nombre", "Tipo", "Raza", "Familiar"};
        modeloClientes=new DefaultTableModel(columnasClientes,0);
        tablaClientes.setModel(modeloClientes);
        

        cbDniFami.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                 if(cbDniFami.getEditor().getItem().toString().length()>0)
                 {
                     String dni=cbDniFami.getEditor().getItem().toString();

                     cbDniFami.removeAllItems();
                     cbDniFami.addItem(dni);

                     Iterator familiares = Consultas.recuperarPersonasPordni(dni);
                     while(familiares.hasNext())
                     {
                        try{
                            C_Persona familiar=(C_Familiar)familiares.next();
                            cbDniFami.addItem(familiar.getDni());
                        }catch(Exception e){}
                     }
                 }
                 //else{
                 //    cargarFamiliares();
                 //}
                
            }
         });
        
        cbCitaDni.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                 if(cbCitaDni.getEditor().getItem().toString().length()>0)
                 {
                     String dni=cbCitaDni.getEditor().getItem().toString();

                     cbCitaDni.removeAllItems();
                     cbCitaDni.addItem(dni);

                     Iterator familiares = Consultas.recuperarPersonasPordni(dni);
                     while(familiares.hasNext())
                     {
                         try{
                            C_Persona familiar=(C_Familiar)familiares.next();
                            cbCitaDni.addItem(familiar.getDni());
                         }catch(Exception e){}
                     }
                 }
                 //else{
                 //    cargarFamiliares();
                 //}
                
            }
         });
        
        
    }
    public static void cargarFamiliares() {
        
        cbDniFami.removeAllItems();
        cbDniFami.addItem("");
        
        try{
            Iterator familiares = Consultas.cargarFamiliares();
            
            while(familiares.hasNext())
            {
                C_Persona familiar=(C_Familiar)familiares.next();
                cbDniFami.addItem(familiar.getDni());
            }
         
        }catch (Exception e) {
            
            System.out.println(e.getMessage());
        }  
    }
    
    public static void cargarCitasLibres(){
        
        Calendar cal=Calendar.getInstance();
        
        cbCitaDia.removeAllItems();
        cbCitaHora.removeAllItems();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        Date ultimodia=cal.getTime();
        Date today=new Date();
        while(today.before(ultimodia))
        {
            cal.setTime(today);
            cbCitaDia.addItem(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            cal.add(Calendar.DATE, 1);
            today=cal.getTime();
        }
        
        for (String hora : horas) {
            cbCitaHora.addItem(hora);
        }
        
        cbCitaMes.setSelectedIndex(cal.get(Calendar.MONTH));
    }
    
    private static void cambiarFechasCitas(){
        

        Calendar cal=Calendar.getInstance();
        cal.setTime(fechaInicio);
        String mes = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String dia=" "+cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(fechaFin);
        String fin=" "+cal.get(Calendar.DAY_OF_MONTH);
        txSemana.setText(" del "+dia+" al "+fin);
        txmesc.setText(mes);

    }
    
    
    public static void cargarCitas() {
           
        
        citas = new Object[38][8];
        int numCitas=0;
        
        try{
            
            Iterator citasit = Consultas.cargarCitas();
            
            while(citasit.hasNext())
            {
                C_Cita cita=(C_Cita)citasit.next();
                
                Date fecha = cita.getFecha();
                
                if(fecha.after(fechaInicio) && fecha.before(fechaFin))
                   
                {
                    Calendar c = Calendar.getInstance();
                    c.setTime(fecha);
                    numCitas++;
                    
                    int dia=c.get(Calendar.DAY_OF_WEEK)-1;
                    String minutos;
                    if(c.get(Calendar.MINUTE)==0)
                        minutos="00";
                    else
                        minutos=String.valueOf(c.get(Calendar.MINUTE));
                    
                    String hora=c.get(Calendar.HOUR_OF_DAY)+":"+minutos;
                    System.out.println("la cita es en un:"+dia+"para las "+hora);
                    
                    for (int i=0;i<horas.length;i++)
                        if(horas[i].compareToIgnoreCase(hora)==0){
                            citas[i][dia]=cita;
                            break;
                        }  
                }
                
            }
            
            for(int i=0;i<horas.length;i++){
                citas[i][0]=horas[i];
            }
        
        String[] columnasCitas=new String[]{"   ", "Lunes ","Martes"," Miercoles", "Jueves" , "Viernes" , "Sabado"};
        modeloCitas=new DefaultTableModel(citas, columnasCitas){
            @Override
            public boolean isCellEditable(int row, int column) {
                //no quiero que se edite nada!!! 
                return false;
            }
        };
        //tablaCitas.setModel(modeloCitas);
        configurarCalendario();
        cambiarFechasCitas();
        
        txNumCitas.setText(""+numCitas);
         
        }catch (Exception e) {
            
            System.out.println(e.getMessage());
        }
    }

    public static void cargarAnimales(){
        
        try{
            
            Iterator animales = Consultas.cargarAnimales();
            
            while(animales.hasNext())
            {
                C_Animal animal=(C_Animal)animales.next();
                
                int id=animal.getId();
                String nombre=animal.getNombre();
                String tipo=animal.getTipo();
                String raza=animal.getRaza();
                String familiar= animal.getFamiliar().getNombre();
                
                Object[] fila= {id, nombre,tipo,raza,familiar};
                modeloClientes.addRow(fila);
            }

        }catch (Exception e) {
            
            System.out.println(e.getMessage());
        }
        
    }
    
    public void cambiarPanel(JPanel panel){
        List<JPanel> paneles=new LinkedList<>();
        paneles.add(panelClientes);
        paneles.add(panelCitas);
        paneles.add(panelVeterinarios);
        paneles.add(panelFacturas);
        paneles.add(panelInventario);
        paneles.add(panelDiagnosticos);
        
        for(int i=0;i<paneles.size();i++){
            if (paneles.get(i).isVisible()){
                System.out.println("tengo un panel visible q es"+paneles.get(i));
                paneles.get(i).getParent().add(panel);
//              //OJOO!! REVISAR exceso de paneles
                //paneles.get(i).getParent().remove(paneles.get(i));
                 
                paneles.get(i).setVisible(false);
                panel.setSize(paneles.get(i).getSize());
                panel.setLocation(paneles.get(i).getLocation());
                panel.setVisible(true);
                break;
            }
        }
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frameClientes = new javax.swing.JFrame();
        btnClientes = new javax.swing.JButton();
        btnCitas = new javax.swing.JButton();
        btnVeterinarios = new javax.swing.JButton();
        btnDiagnosticos = new javax.swing.JButton();
        btnFacturas = new javax.swing.JButton();
        btnInventario = new javax.swing.JButton();
        btnInfo = new javax.swing.JButton();
        panelClientes = new javax.swing.JPanel();
        panelDatosClientes = new javax.swing.JPanel();
        btnEditarCli = new javax.swing.JButton();
        btnEliminarCli = new javax.swing.JButton();
        btnNuevoCli = new javax.swing.JButton();
        scrollTabla = new javax.swing.JScrollPane();
        tablaClientes = new javax.swing.JTable();
        panelVerClientes = new javax.swing.JPanel();
        btnVerCli = new javax.swing.JButton();
        btnCitaCli = new javax.swing.JButton();
        panelContactoClientes = new javax.swing.JPanel();
        btnMailCli = new javax.swing.JButton();
        btnContactoCli = new javax.swing.JButton();
        panelFiltrosClientes = new javax.swing.JPanel();
        txtFiltro = new javax.swing.JTextField();
        cbFiltro = new javax.swing.JComboBox<>();
        rbgSexo = new javax.swing.ButtonGroup();
        panelVeterinarios = new javax.swing.JPanel();
        panelContactoClientes1 = new javax.swing.JPanel();
        btnMailCli1 = new javax.swing.JButton();
        btnContactoCli1 = new javax.swing.JButton();
        txtNombreVet = new javax.swing.JTextField();
        lbNombreVet = new javax.swing.JLabel();
        panelDatosVet = new javax.swing.JPanel();
        btnEliminarVet = new javax.swing.JButton();
        btnEditarVet = new javax.swing.JButton();
        btnNuevoVet = new javax.swing.JButton();
        panelVerClientes1 = new javax.swing.JPanel();
        btnVerCli1 = new javax.swing.JButton();
        btnCitaCli1 = new javax.swing.JButton();
        scrollTabla1 = new javax.swing.JScrollPane();
        tablaVeterinarios = new javax.swing.JTable();
        panelCitas = new javax.swing.JPanel();
        panelContactoClientes2 = new javax.swing.JPanel();
        lbSemana = new javax.swing.JLabel();
        lbNCitas = new javax.swing.JLabel();
        txSemana = new javax.swing.JLabel();
        txNumCitas = new javax.swing.JLabel();
        lbmm = new javax.swing.JLabel();
        txmesc = new javax.swing.JLabel();
        panelDatosClientes2 = new javax.swing.JPanel();
        btnEliminarCita = new javax.swing.JButton();
        btnEditarCita = new javax.swing.JButton();
        btnNuevaCita = new javax.swing.JButton();
        panelVerClientes2 = new javax.swing.JPanel();
        btnVerCita = new javax.swing.JButton();
        lbFechasCita = new javax.swing.JLabel();
        panelCitasList = new javax.swing.JScrollPane();
        tablaCitas = new javax.swing.JTable();
        cbCitaFiltroMes = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        cbCitaFiltroSemana = new javax.swing.JComboBox<>();
        panelFacturas = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnModificarFact = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        lbFechaFactura = new javax.swing.JLabel();
        txtFechaFactura = new javax.swing.JTextField();
        panelDatosFacturas = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        chkConsulta = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtFactTlf = new javax.swing.JTextField();
        txtDniFact = new javax.swing.JTextField();
        txtNombreFact = new javax.swing.JTextField();
        txtMailFact = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        txtNumFactura = new javax.swing.JTextField();
        lbNumFactura = new javax.swing.JLabel();
        panelInventario = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel7 = new javax.swing.JPanel();
        imgProducto = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btnComprar = new javax.swing.JButton();
        panelDatosClientes3 = new javax.swing.JPanel();
        btnEliminarCli3 = new javax.swing.JButton();
        btnEditarCli3 = new javax.swing.JButton();
        btnVenta = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        lbIDInv = new javax.swing.JLabel();
        lbPrecioInv = new javax.swing.JLabel();
        txtIDInv = new javax.swing.JTextField();
        txtPrecioInv = new javax.swing.JTextField();
        panelDiagnosticos = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        listaCitasVet = new javax.swing.JList<>();
        panelDatosDiag = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        txhoraCitaDiag = new javax.swing.JLabel();
        txTipoDiag = new javax.swing.JLabel();
        txSexoDiag = new javax.swing.JLabel();
        txNombreAniDiag = new javax.swing.JLabel();
        txRazaDiag = new javax.swing.JLabel();
        txFechaNacDiag = new javax.swing.JLabel();
        txAsuntoDiag = new javax.swing.JLabel();
        panelTratamientos = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        txTratamiento = new javax.swing.JTextArea();
        jScrollPane9 = new javax.swing.JScrollPane();
        txDiagnostico = new javax.swing.JTextArea();
        lbtrat = new javax.swing.JLabel();
        tbdesc = new javax.swing.JLabel();
        chkMedicacion = new javax.swing.JCheckBox();
        jScrollPane12 = new javax.swing.JScrollPane();
        listaMedicamentosBD = new javax.swing.JList<>();
        txtFiltroMed = new javax.swing.JTextField();
        lbFlt = new javax.swing.JLabel();
        btnMedicamento = new javax.swing.JButton();
        btnEliminarMed = new javax.swing.JButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        listaMedicamentos = new javax.swing.JList<>();
        btnFinDiag = new javax.swing.JButton();
        cbVetDiagnosticos = new javax.swing.JComboBox<>();
        lbCitasVeterinario = new javax.swing.JLabel();
        dialogEditClientes = new javax.swing.JDialog();
        btnAceptar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panelDatosFamiliar = new javax.swing.JPanel();
        lbDireFami = new javax.swing.JLabel();
        txtDireFami = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lbMailFami = new javax.swing.JLabel();
        lbTlfFami = new javax.swing.JLabel();
        txtTlfFami = new javax.swing.JTextField();
        txtMailFami = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        lbDniCli = new javax.swing.JLabel();
        lbNombreFami = new javax.swing.JLabel();
        txtNombreFami = new javax.swing.JTextField();
        cbDniFami = new javax.swing.JComboBox<>();
        PanelDatosMed = new javax.swing.JPanel();
        lbPesoCli = new javax.swing.JLabel();
        txtPesoCli = new javax.swing.JTextField();
        panelVacunas = new javax.swing.JPanel();
        cbVacuna1 = new javax.swing.JCheckBox();
        cbVacuna2 = new javax.swing.JCheckBox();
        cbVacuna3 = new javax.swing.JCheckBox();
        cbVacuna4 = new javax.swing.JCheckBox();
        cbVacuna5 = new javax.swing.JCheckBox();
        cbVacuna6 = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtComentarioCli = new javax.swing.JTextArea();
        lbComentarioCli = new javax.swing.JLabel();
        lbFecha_nacAni = new javax.swing.JLabel();
        txtFechaCli = new javax.swing.JTextField();
        panelDatosAnimal = new javax.swing.JPanel();
        lbNombreAni = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cbTipoAni = new javax.swing.JComboBox<>();
        cbRazaAni = new javax.swing.JComboBox<>();
        lbTipoAni = new javax.swing.JLabel();
        lbRazaAni = new javax.swing.JLabel();
        txtChipidCli = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        newRbHembra = new javax.swing.JRadioButton();
        newRbMacho = new javax.swing.JRadioButton();
        txtNombreCli = new javax.swing.JTextField();
        dialogEditCitas = new javax.swing.JDialog();
        panelDatosFamiliar5 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        lbMailFami5 = new javax.swing.JLabel();
        lbTlfFami5 = new javax.swing.JLabel();
        txtCitaTlfFami = new javax.swing.JTextField();
        txtCitaMailFami = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        lbDniCli2 = new javax.swing.JLabel();
        lbNombreFami2 = new javax.swing.JLabel();
        txtCitaNombreFami = new javax.swing.JTextField();
        cbCitaDni = new javax.swing.JComboBox<>();
        panelDatosCita = new javax.swing.JPanel();
        panelDatosAniCita = new javax.swing.JPanel();
        lbCitaNom = new javax.swing.JLabel();
        lbCitaTipo = new javax.swing.JLabel();
        lbCitaRaza = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtCitaAsunto = new javax.swing.JTextArea();
        lbCitaAsunto = new javax.swing.JLabel();
        txtCitaTipo = new javax.swing.JTextField();
        txtCitaRaza = new javax.swing.JTextField();
        cbCitaNombreAni = new javax.swing.JComboBox<>();
        lbCitaId = new javax.swing.JLabel();
        txtCitaId = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        cbCitaDia = new javax.swing.JComboBox<>();
        cbCitaHora = new javax.swing.JComboBox<>();
        lbResumenCita = new javax.swing.JLabel();
        cbCitaMes = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtResumenCita = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cbVetCita = new javax.swing.JComboBox<>();
        lbVetCita = new javax.swing.JLabel();
        btnCitaAceptar = new javax.swing.JButton();
        btnCitaCancel = new javax.swing.JButton();
        dialogFichaAnimal = new javax.swing.JDialog();
        lbFotoPerfil = new javax.swing.JLabel();
        panelDatosFichaCli = new javax.swing.JPanel();
        lbID = new javax.swing.JLabel();
        lbNomb = new javax.swing.JLabel();
        lbEspecie = new javax.swing.JLabel();
        lbRaza = new javax.swing.JLabel();
        lbSexo = new javax.swing.JLabel();
        lbFechaNac = new javax.swing.JLabel();
        txID = new javax.swing.JLabel();
        txNomb = new javax.swing.JLabel();
        txEspecie = new javax.swing.JLabel();
        txRaza = new javax.swing.JLabel();
        txSexo = new javax.swing.JLabel();
        txFechaNac = new javax.swing.JLabel();
        panelDatosClinicos = new javax.swing.JPanel();
        lbPeso = new javax.swing.JLabel();
        txPeso = new javax.swing.JLabel();
        lbVacunas = new javax.swing.JLabel();
        txVacunas = new javax.swing.JLabel();
        lbComentarios = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        txComentario = new javax.swing.JTextArea();
        dialogEditVet = new javax.swing.JDialog();
        jPanel10 = new javax.swing.JPanel();
        txtVetTlf = new javax.swing.JTextField();
        lbVetDni = new javax.swing.JLabel();
        txtVetDni = new javax.swing.JTextField();
        lbVetEmail = new javax.swing.JLabel();
        txtNumLicencia = new javax.swing.JTextField();
        lbNum_Licencia = new javax.swing.JLabel();
        txtVetEmail = new javax.swing.JTextField();
        btnVetCancelar = new javax.swing.JButton();
        lbVetNombre = new javax.swing.JLabel();
        btnVetAceptar = new javax.swing.JButton();
        txtVetNombre = new javax.swing.JTextField();
        lbVetTlf = new javax.swing.JLabel();
        dialogContacto = new javax.swing.JDialog();
        icContacto = new javax.swing.JLabel();
        lbDNI = new javax.swing.JLabel();
        lbNombre = new javax.swing.JLabel();
        lbTel = new javax.swing.JLabel();
        lbEmail = new javax.swing.JLabel();
        lbDir = new javax.swing.JLabel();
        txDNI = new javax.swing.JLabel();
        txNombre = new javax.swing.JLabel();
        txTel = new javax.swing.JLabel();
        txEmail = new javax.swing.JLabel();
        txDir = new javax.swing.JLabel();
        dialogDetallesCita = new javax.swing.JDialog();
        lbVet = new javax.swing.JLabel();
        lbCli = new javax.swing.JLabel();
        lbHora = new javax.swing.JLabel();
        lbFam = new javax.swing.JLabel();
        lbAsunto = new javax.swing.JLabel();
        txVet = new javax.swing.JLabel();
        txCli = new javax.swing.JLabel();
        txFam = new javax.swing.JLabel();
        txHora = new javax.swing.JLabel();
        txAsunto = new javax.swing.JLabel();
        lbTelFam = new javax.swing.JLabel();
        txTelFam = new javax.swing.JLabel();
        dialogUltimosDiagnosticos = new javax.swing.JDialog();
        comboBox5Ultimos = new javax.swing.JComboBox<>();
        panelDetalles = new javax.swing.JPanel();
        lbDiagAnimal = new javax.swing.JLabel();
        lbDiagDiagnostico = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        txaDiagnostico = new javax.swing.JTextArea();
        lbTipTratamiento = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        txaTratamiento = new javax.swing.JTextArea();
        lbContacto = new javax.swing.JLabel();
        lbMedicacion = new javax.swing.JLabel();
        txaMedicacion = new javax.swing.JLabel();
        txaNombre = new javax.swing.JLabel();
        txaContacto = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        dialogConfiguracion = new javax.swing.JDialog();
        panelConfigDB = new javax.swing.JPanel();
        lbGestor = new javax.swing.JLabel();
        cbGestor = new javax.swing.JComboBox<>();
        lbURL = new javax.swing.JLabel();
        txURL = new javax.swing.JTextField();
        txUsuario = new javax.swing.JTextField();
        txContraseña = new javax.swing.JTextField();
        lbUsuario = new javax.swing.JLabel();
        lbContraseña = new javax.swing.JLabel();
        lbIconoGestor = new javax.swing.JLabel();
        btCancelarConfig = new javax.swing.JButton();
        btAceptarConfig = new javax.swing.JButton();
        checkDemo = new javax.swing.JCheckBox();
        dialogInfo = new javax.swing.JDialog();
        panelInfo = new javax.swing.JPanel();
        mypets = new javax.swing.JLabel();
        lbCreado = new javax.swing.JLabel();
        lbDavid = new javax.swing.JLabel();
        lbMiguel = new javax.swing.JLabel();
        lbSaul = new javax.swing.JLabel();
        lbModulo = new javax.swing.JLabel();
        lbAAD = new javax.swing.JLabel();
        lbCiclo = new javax.swing.JLabel();
        lbDAM = new javax.swing.JLabel();
        lbAño = new javax.swing.JLabel();
        lb16_17 = new javax.swing.JLabel();
        dialogPremium = new javax.swing.JDialog();
        jLabel17 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lbLogo = new javax.swing.JLabel();
        pbLoading = new javax.swing.JProgressBar();
        btnPlay = new javax.swing.JButton();
        btnConfig = new javax.swing.JButton();

        btnClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/pawprint.png"))); // NOI18N
        btnClientes.setText("Clientes");
        btnClientes.setToolTipText("");
        btnClientes.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientesActionPerformed(evt);
            }
        });

        btnCitas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/calendario.png"))); // NOI18N
        btnCitas.setText("Citas");
        btnCitas.setToolTipText("");
        btnCitas.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCitas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCitasActionPerformed(evt);
            }
        });

        btnVeterinarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/doctor.png"))); // NOI18N
        btnVeterinarios.setText("Veterinarios");
        btnVeterinarios.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnVeterinarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVeterinariosActionPerformed(evt);
            }
        });

        btnDiagnosticos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/medicina.png"))); // NOI18N
        btnDiagnosticos.setText("Diagnosticos");
        btnDiagnosticos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosticosActionPerformed(evt);
            }
        });

        btnFacturas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/recepcion.png"))); // NOI18N
        btnFacturas.setText("Facturas");
        btnFacturas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFacturasActionPerformed(evt);
            }
        });

        btnInventario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/almacen.png"))); // NOI18N
        btnInventario.setText("Inventario");
        btnInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarioActionPerformed(evt);
            }
        });

        btnInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/servicio-al-cliente big.png"))); // NOI18N
        btnInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInfoActionPerformed(evt);
            }
        });

        panelDatosClientes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnEditarCli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo-nuevo.png"))); // NOI18N
        btnEditarCli.setText("Editar");
        btnEditarCli.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditarCli.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditarCli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarCliActionPerformed(evt);
            }
        });

        btnEliminarCli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo.png"))); // NOI18N
        btnEliminarCli.setText("Eliminar");
        btnEliminarCli.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarCli.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarCli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarCliActionPerformed(evt);
            }
        });

        btnNuevoCli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/anadir-pagina-nueva.png"))); // NOI18N
        btnNuevoCli.setText("Nuevo");
        btnNuevoCli.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevoCli.setMaximumSize(new java.awt.Dimension(65, 65));
        btnNuevoCli.setMinimumSize(new java.awt.Dimension(65, 65));
        btnNuevoCli.setPreferredSize(new java.awt.Dimension(65, 65));
        btnNuevoCli.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevoCli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoCliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatosClientesLayout = new javax.swing.GroupLayout(panelDatosClientes);
        panelDatosClientes.setLayout(panelDatosClientesLayout);
        panelDatosClientesLayout.setHorizontalGroup(
            panelDatosClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNuevoCli, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEditarCli, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminarCli)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDatosClientesLayout.setVerticalGroup(
            panelDatosClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnEditarCli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarCli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNuevoCli, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scrollTabla.setViewportView(tablaClientes);

        panelVerClientes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnVerCli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/reanudar.png"))); // NOI18N
        btnVerCli.setText("Ver Ficha");
        btnVerCli.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVerCli.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVerCli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerCliActionPerformed(evt);
            }
        });

        btnCitaCli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/calendario green.png"))); // NOI18N
        btnCitaCli.setText("Nueva Cita");
        btnCitaCli.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCitaCli.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCitaCli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCitaCliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelVerClientesLayout = new javax.swing.GroupLayout(panelVerClientes);
        panelVerClientes.setLayout(panelVerClientesLayout);
        panelVerClientesLayout.setHorizontalGroup(
            panelVerClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVerClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCitaCli)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnVerCli, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelVerClientesLayout.setVerticalGroup(
            panelVerClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVerClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelVerClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnVerCli, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCitaCli, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelContactoClientes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnMailCli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/mensaje.png"))); // NOI18N
        btnMailCli.setText("Enviar Mail");
        btnMailCli.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMailCli.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnContactoCli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/phone.png"))); // NOI18N
        btnContactoCli.setText("Datos Contacto");
        btnContactoCli.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContactoCli.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnContactoCli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContactoCliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelContactoClientesLayout = new javax.swing.GroupLayout(panelContactoClientes);
        panelContactoClientes.setLayout(panelContactoClientesLayout);
        panelContactoClientesLayout.setHorizontalGroup(
            panelContactoClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContactoClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMailCli, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnContactoCli)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelContactoClientesLayout.setVerticalGroup(
            panelContactoClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContactoClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelContactoClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnMailCli, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .addComponent(btnContactoCli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFiltrosClientes.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtrar Por"));

        txtFiltro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiltroKeyReleased(evt);
            }
        });

        cbFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nombre", "Id" }));
        cbFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFiltroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFiltrosClientesLayout = new javax.swing.GroupLayout(panelFiltrosClientes);
        panelFiltrosClientes.setLayout(panelFiltrosClientesLayout);
        panelFiltrosClientesLayout.setHorizontalGroup(
            panelFiltrosClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltrosClientesLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(cbFiltro, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(txtFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelFiltrosClientesLayout.setVerticalGroup(
            panelFiltrosClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosClientesLayout.createSequentialGroup()
                .addGap(0, 11, Short.MAX_VALUE)
                .addGroup(panelFiltrosClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelClientesLayout = new javax.swing.GroupLayout(panelClientes);
        panelClientes.setLayout(panelClientesLayout);
        panelClientesLayout.setHorizontalGroup(
            panelClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollTabla)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelClientesLayout.createSequentialGroup()
                        .addComponent(panelDatosClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelVerClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelContactoClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panelFiltrosClientes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelClientesLayout.setVerticalGroup(
            panelClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelVerClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelContactoClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelDatosClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(11, 11, 11)
                .addComponent(panelFiltrosClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout frameClientesLayout = new javax.swing.GroupLayout(frameClientes.getContentPane());
        frameClientes.getContentPane().setLayout(frameClientesLayout);
        frameClientesLayout.setHorizontalGroup(
            frameClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(frameClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(frameClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnFacturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnInventario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCitas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnVeterinarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDiagnosticos, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
                    .addComponent(btnInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        frameClientesLayout.setVerticalGroup(
            frameClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(frameClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(frameClientesLayout.createSequentialGroup()
                        .addComponent(btnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCitas, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVeterinarios, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDiagnosticos, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnInfo))
                    .addComponent(panelClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelVeterinarios.setPreferredSize(new java.awt.Dimension(735, 680));

        panelContactoClientes1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelContactoClientes1.setMaximumSize(new java.awt.Dimension(197, 67));
        panelContactoClientes1.setMinimumSize(new java.awt.Dimension(197, 67));
        panelContactoClientes1.setPreferredSize(new java.awt.Dimension(197, 67));

        btnMailCli1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/mensaje.png"))); // NOI18N
        btnMailCli1.setText("Enviar Mail");
        btnMailCli1.setEnabled(false);
        btnMailCli1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMailCli1.setMaximumSize(new java.awt.Dimension(93, 23));
        btnMailCli1.setMinimumSize(new java.awt.Dimension(93, 23));
        btnMailCli1.setPreferredSize(new java.awt.Dimension(93, 23));
        btnMailCli1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMailCli1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMailCli1ActionPerformed(evt);
            }
        });

        btnContactoCli1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/phone.png"))); // NOI18N
        btnContactoCli1.setText("Contacto");
        btnContactoCli1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContactoCli1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout panelContactoClientes1Layout = new javax.swing.GroupLayout(panelContactoClientes1);
        panelContactoClientes1.setLayout(panelContactoClientes1Layout);
        panelContactoClientes1Layout.setHorizontalGroup(
            panelContactoClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContactoClientes1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMailCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnContactoCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(9, 9, 9))
        );
        panelContactoClientes1Layout.setVerticalGroup(
            panelContactoClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContactoClientes1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelContactoClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMailCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnContactoCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        lbNombreVet.setText("Nombre:");

        panelDatosVet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelDatosVet.setMaximumSize(new java.awt.Dimension(227, 67));
        panelDatosVet.setMinimumSize(new java.awt.Dimension(227, 67));

        btnEliminarVet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo.png"))); // NOI18N
        btnEliminarVet.setText("Eliminar");
        btnEliminarVet.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarVet.setMaximumSize(new java.awt.Dimension(41, 55));
        btnEliminarVet.setMinimumSize(new java.awt.Dimension(41, 55));
        btnEliminarVet.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarVet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarVetActionPerformed(evt);
            }
        });

        btnEditarVet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo-nuevo.png"))); // NOI18N
        btnEditarVet.setText("Editar");
        btnEditarVet.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditarVet.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditarVet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarVetActionPerformed(evt);
            }
        });

        btnNuevoVet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/anadir-pagina-nueva.png"))); // NOI18N
        btnNuevoVet.setText("Nuevo");
        btnNuevoVet.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevoVet.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevoVet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoVetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatosVetLayout = new javax.swing.GroupLayout(panelDatosVet);
        panelDatosVet.setLayout(panelDatosVetLayout);
        panelDatosVetLayout.setHorizontalGroup(
            panelDatosVetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosVetLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btnNuevoVet, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditarVet, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminarVet, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );
        panelDatosVetLayout.setVerticalGroup(
            panelDatosVetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosVetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosVetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnNuevoVet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarVet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditarVet, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelVerClientes1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelVerClientes1.setMaximumSize(new java.awt.Dimension(278, 67));
        panelVerClientes1.setMinimumSize(new java.awt.Dimension(278, 67));
        panelVerClientes1.setPreferredSize(new java.awt.Dimension(278, 67));

        btnVerCli1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/reanudar.png"))); // NOI18N
        btnVerCli1.setText("Ult. Diagnosticos");
        btnVerCli1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVerCli1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVerCli1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerCli1ActionPerformed(evt);
            }
        });

        btnCitaCli1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/calendario green.png"))); // NOI18N
        btnCitaCli1.setText("Crear Cita");
        btnCitaCli1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCitaCli1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout panelVerClientes1Layout = new javax.swing.GroupLayout(panelVerClientes1);
        panelVerClientes1.setLayout(panelVerClientes1Layout);
        panelVerClientes1Layout.setHorizontalGroup(
            panelVerClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVerClientes1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnVerCli1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCitaCli1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelVerClientes1Layout.setVerticalGroup(
            panelVerClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVerClientes1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelVerClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnCitaCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVerCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tablaVeterinarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scrollTabla1.setViewportView(tablaVeterinarios);

        javax.swing.GroupLayout panelVeterinariosLayout = new javax.swing.GroupLayout(panelVeterinarios);
        panelVeterinarios.setLayout(panelVeterinariosLayout);
        panelVeterinariosLayout.setHorizontalGroup(
            panelVeterinariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVeterinariosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelVeterinariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollTabla1)
                    .addGroup(panelVeterinariosLayout.createSequentialGroup()
                        .addComponent(lbNombreVet)
                        .addGap(18, 18, 18)
                        .addComponent(txtNombreVet, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelVeterinariosLayout.createSequentialGroup()
                        .addComponent(panelDatosVet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelVerClientes1, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(panelContactoClientes1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelVeterinariosLayout.setVerticalGroup(
            panelVeterinariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVeterinariosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelVeterinariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelContactoClientes1, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(panelVerClientes1, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(panelDatosVet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelVeterinariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNombreVet)
                    .addComponent(txtNombreVet))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollTabla1, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelCitas.setPreferredSize(new java.awt.Dimension(735, 680));

        panelContactoClientes2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelContactoClientes2.setMinimumSize(new java.awt.Dimension(197, 67));
        panelContactoClientes2.setPreferredSize(new java.awt.Dimension(197, 67));

        lbSemana.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbSemana.setText("Semana:");

        lbNCitas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbNCitas.setText("Nº de citas de esta semana:");

        txSemana.setText("txSemana");

        txNumCitas.setText("5555");

        lbmm.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbmm.setText("Mes:");

        txmesc.setText("mesdemarzo");

        javax.swing.GroupLayout panelContactoClientes2Layout = new javax.swing.GroupLayout(panelContactoClientes2);
        panelContactoClientes2.setLayout(panelContactoClientes2Layout);
        panelContactoClientes2Layout.setHorizontalGroup(
            panelContactoClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContactoClientes2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelContactoClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelContactoClientes2Layout.createSequentialGroup()
                        .addComponent(lbNCitas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txNumCitas))
                    .addGroup(panelContactoClientes2Layout.createSequentialGroup()
                        .addComponent(lbSemana)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txSemana))
                    .addGroup(panelContactoClientes2Layout.createSequentialGroup()
                        .addComponent(lbmm)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txmesc)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelContactoClientes2Layout.setVerticalGroup(
            panelContactoClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContactoClientes2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelContactoClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbmm)
                    .addComponent(txmesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelContactoClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txSemana, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbSemana, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelContactoClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNCitas)
                    .addComponent(txNumCitas))
                .addContainerGap())
        );

        panelDatosClientes2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelDatosClientes2.setMinimumSize(new java.awt.Dimension(0, 0));
        panelDatosClientes2.setPreferredSize(new java.awt.Dimension(249, 91));

        btnEliminarCita.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo.png"))); // NOI18N
        btnEliminarCita.setText("Eliminar");
        btnEliminarCita.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarCita.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarCitaActionPerformed(evt);
            }
        });

        btnEditarCita.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo-nuevo.png"))); // NOI18N
        btnEditarCita.setText("Editar");
        btnEditarCita.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditarCita.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditarCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarCitaActionPerformed(evt);
            }
        });

        btnNuevaCita.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/anadir-pagina-nueva.png"))); // NOI18N
        btnNuevaCita.setText("Nuevo");
        btnNuevaCita.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevaCita.setMaximumSize(new java.awt.Dimension(65, 65));
        btnNuevaCita.setMinimumSize(new java.awt.Dimension(65, 65));
        btnNuevaCita.setPreferredSize(new java.awt.Dimension(65, 65));
        btnNuevaCita.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevaCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaCitaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatosClientes2Layout = new javax.swing.GroupLayout(panelDatosClientes2);
        panelDatosClientes2.setLayout(panelDatosClientes2Layout);
        panelDatosClientes2Layout.setHorizontalGroup(
            panelDatosClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosClientes2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNuevaCita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditarCita)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminarCita)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDatosClientes2Layout.setVerticalGroup(
            panelDatosClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosClientes2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNuevaCita, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnEditarCita, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarCita, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelVerClientes2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelVerClientes2.setMaximumSize(new java.awt.Dimension(278, 47));
        panelVerClientes2.setMinimumSize(new java.awt.Dimension(278, 47));

        btnVerCita.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/reanudar.png"))); // NOI18N
        btnVerCita.setText("Ver Detalles");
        btnVerCita.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVerCita.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVerCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerCitaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelVerClientes2Layout = new javax.swing.GroupLayout(panelVerClientes2);
        panelVerClientes2.setLayout(panelVerClientes2Layout);
        panelVerClientes2Layout.setHorizontalGroup(
            panelVerClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVerClientes2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnVerCita)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelVerClientes2Layout.setVerticalGroup(
            panelVerClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelVerClientes2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnVerCita, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        lbFechasCita.setText("Seleccione mes:");

        tablaCitas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaCitas.setSelectionBackground(new java.awt.Color(255, 255, 204));
        tablaCitas.setSelectionForeground(new java.awt.Color(0, 0, 0));
        tablaCitas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        panelCitasList.setViewportView(tablaCitas);

        cbCitaFiltroMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));
        cbCitaFiltroMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCitaFiltroMesActionPerformed(evt);
            }
        });

        jLabel20.setText("Seleccione semana:");

        cbCitaFiltroSemana.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semana 1", "Semana 2", "Semana 3", "Semana 4" }));
        cbCitaFiltroSemana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCitaFiltroSemanaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelCitasLayout = new javax.swing.GroupLayout(panelCitas);
        panelCitas.setLayout(panelCitasLayout);
        panelCitasLayout.setHorizontalGroup(
            panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCitasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCitasList, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                    .addGroup(panelCitasLayout.createSequentialGroup()
                        .addGroup(panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelCitasLayout.createSequentialGroup()
                                .addComponent(lbFechasCita)
                                .addGap(18, 18, 18)
                                .addComponent(cbCitaFiltroMes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(panelDatosClientes2, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelCitasLayout.createSequentialGroup()
                                .addGap(86, 86, 86)
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(cbCitaFiltroSemana, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(panelCitasLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelVerClientes2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelContactoClientes2, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        panelCitasLayout.setVerticalGroup(
            panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCitasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(panelVerClientes2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelDatosClientes2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                    .addComponent(panelContactoClientes2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbFechasCita)
                    .addComponent(cbCitaFiltroMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(cbCitaFiltroSemana, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelCitasList, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelFacturas.setPreferredSize(new java.awt.Dimension(735, 686));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnModificarFact.setText("Modificar");

        jButton1.setText("Añadir");

        jButton3.setText("Eliminar");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnModificarFact, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModificarFact, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jList1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        lbFechaFactura.setText("Fecha:");

        txtFechaFactura.setText(" ");

        jLabel2.setText("Total:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("654.5 €");

        jLabel5.setText("SubTotal:");

        jLabel6.setText("Descuento:");

        jLabel7.setText("Impuestos:");

        jLabel8.setText("670 y tantos");

        jLabel10.setText("0");

        jLabel11.setText("25.12");

        javax.swing.GroupLayout panelDatosFacturasLayout = new javax.swing.GroupLayout(panelDatosFacturas);
        panelDatosFacturas.setLayout(panelDatosFacturasLayout);
        panelDatosFacturasLayout.setHorizontalGroup(
            panelDatosFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelDatosFacturasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDatosFacturasLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelDatosFacturasLayout.createSequentialGroup()
                        .addGroup(panelDatosFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelDatosFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(panelDatosFacturasLayout.createSequentialGroup()
                                .addGap(55, 55, 55)
                                .addComponent(jLabel10))
                            .addGroup(panelDatosFacturasLayout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(jLabel11)))))
                .addContainerGap())
        );
        panelDatosFacturasLayout.setVerticalGroup(
            panelDatosFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosFacturasLayout.createSequentialGroup()
                .addContainerGap(210, Short.MAX_VALUE)
                .addGroup(panelDatosFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        chkConsulta.setText("Incluir Consulta");

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Receptor"));

        jLabel9.setText("Dni:");

        jLabel14.setText("Nombre:");

        jLabel15.setText("Teléfono:");

        jLabel16.setText("Email:");

        txtFactTlf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFactTlfActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDniFact, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                    .addComponent(txtFactTlf))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNombreFact)
                    .addComponent(txtMailFact, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNombreFact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(txtMailFact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtDniFact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtFactTlf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        txtNumFactura.setText(" ");

        lbNumFactura.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbNumFactura.setText("FACTURA Nº:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbNumFactura)
                .addGap(148, 148, 148))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtNumFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbNumFactura)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNumFactura)
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout panelFacturasLayout = new javax.swing.GroupLayout(panelFacturas);
        panelFacturas.setLayout(panelFacturasLayout);
        panelFacturasLayout.setHorizontalGroup(
            panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFacturasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFacturasLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelFacturasLayout.createSequentialGroup()
                                .addComponent(lbFechaFactura)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtFechaFactura))
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFacturasLayout.createSequentialGroup()
                                .addComponent(chkConsulta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, Short.MAX_VALUE))
                            .addGroup(panelFacturasLayout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(panelDatosFacturas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelFacturasLayout.setVerticalGroup(
            panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFacturasLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(panelFacturasLayout.createSequentialGroup()
                        .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbFechaFactura)
                            .addComponent(txtFechaFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkConsulta))
                        .addGap(9, 9, 9)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(panelDatosFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Almacen");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Accesorios");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Ropa");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Alimentos");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Snaks");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Piensos");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Casetas");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Baratas");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Caras");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Medicamentos");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Antiparasitos");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Medicamentos");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Vacunas");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Paseo");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Correas");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Transporte");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Correas");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Arneses");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Peluqueria e Higiene");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Cepillos");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane3.setViewportView(jTree1);

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        imgProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/seresto.png"))); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText("Collar con efecto repelente y acaricida constante contra \nlas garrapatas y con efecto letal contra las \npulgas en los perros de más de 7 semanas.");
        jScrollPane6.setViewportView(jTextArea1);

        btnComprar.setText("Comprar");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnComprar)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(imgProducto)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(imgProducto)
                .addGap(32, 32, 32)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addComponent(btnComprar)
                .addContainerGap())
        );

        panelDatosClientes3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelDatosClientes3.setMaximumSize(new java.awt.Dimension(227, 67));
        panelDatosClientes3.setMinimumSize(new java.awt.Dimension(227, 67));

        btnEliminarCli3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo.png"))); // NOI18N
        btnEliminarCli3.setText("Eliminar");
        btnEliminarCli3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarCli3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnEditarCli3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo-nuevo.png"))); // NOI18N
        btnEditarCli3.setText("Editar");
        btnEditarCli3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditarCli3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/anadir-pagina-nueva.png"))); // NOI18N
        btnVenta.setText("Nuevo");
        btnVenta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVenta.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatosClientes3Layout = new javax.swing.GroupLayout(panelDatosClientes3);
        panelDatosClientes3.setLayout(panelDatosClientes3Layout);
        panelDatosClientes3Layout.setHorizontalGroup(
            panelDatosClientes3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosClientes3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnVenta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditarCli3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminarCli3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDatosClientes3Layout.setVerticalGroup(
            panelDatosClientes3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosClientes3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosClientes3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditarCli3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarCli3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lbIDInv.setText("ID Producto:");

        lbPrecioInv.setText("Precio:");

        txtIDInv.setText("0122140");

        txtPrecioInv.setText("22,50€");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbIDInv)
                    .addComponent(lbPrecioInv))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtIDInv, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(txtPrecioInv))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbIDInv)
                    .addComponent(txtIDInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbPrecioInv)
                    .addComponent(txtPrecioInv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelInventarioLayout = new javax.swing.GroupLayout(panelInventario);
        panelInventario.setLayout(panelInventarioLayout);
        panelInventarioLayout.setHorizontalGroup(
            panelInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInventarioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInventarioLayout.createSequentialGroup()
                        .addComponent(panelDatosClientes3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelInventarioLayout.setVerticalGroup(
            panelInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInventarioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInventarioLayout.createSequentialGroup()
                        .addGroup(panelInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelDatosClientes3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );

        panelDiagnosticos.setPreferredSize(new java.awt.Dimension(735, 680));

        listaCitasVet.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listaCitasVet.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listaCitasVetValueChanged(evt);
            }
        });
        jScrollPane7.setViewportView(listaCitasVet);

        panelDatosDiag.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos"));

        txhoraCitaDiag.setText("hora_Cita");

        txTipoDiag.setText("Tipo_Animal");

        txSexoDiag.setText("Sexo");

        txNombreAniDiag.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txNombreAniDiag.setText("Nombre_Animal");

        txRazaDiag.setText("Raza");

        txFechaNacDiag.setText("Fecha_Nac");

        txAsuntoDiag.setText("Asunto");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txSexoDiag)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(txNombreAniDiag)
                        .addGap(39, 39, 39)
                        .addComponent(txhoraCitaDiag))
                    .addComponent(txTipoDiag)
                    .addComponent(txRazaDiag)
                    .addComponent(txFechaNacDiag)
                    .addComponent(txAsuntoDiag))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txhoraCitaDiag)
                    .addComponent(txNombreAniDiag))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txSexoDiag)
                .addGap(11, 11, 11)
                .addComponent(txTipoDiag)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txRazaDiag)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txFechaNacDiag)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txAsuntoDiag)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelDatosDiagLayout = new javax.swing.GroupLayout(panelDatosDiag);
        panelDatosDiag.setLayout(panelDatosDiagLayout);
        panelDatosDiagLayout.setHorizontalGroup(
            panelDatosDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosDiagLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelDatosDiagLayout.setVerticalGroup(
            panelDatosDiagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosDiagLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelTratamientos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txTratamiento.setColumns(20);
        txTratamiento.setRows(5);
        jScrollPane8.setViewportView(txTratamiento);

        txDiagnostico.setColumns(20);
        txDiagnostico.setRows(5);
        jScrollPane9.setViewportView(txDiagnostico);

        lbtrat.setText("Tratamiento:");

        tbdesc.setText("Diagnóstico:");

        chkMedicacion.setText("Medicación");
        chkMedicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMedicacionActionPerformed(evt);
            }
        });

        listaMedicamentosBD.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane12.setViewportView(listaMedicamentosBD);

        txtFiltroMed.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiltroMedKeyReleased(evt);
            }
        });

        lbFlt.setText("Medicamento:");

        btnMedicamento.setText("Añadir");
        btnMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMedicamentoActionPerformed(evt);
            }
        });

        btnEliminarMed.setText("Eliminar");
        btnEliminarMed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarMedActionPerformed(evt);
            }
        });

        listaMedicamentos.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane11.setViewportView(listaMedicamentos);

        btnFinDiag.setText("Finalizar");
        btnFinDiag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinDiagActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTratamientosLayout = new javax.swing.GroupLayout(panelTratamientos);
        panelTratamientos.setLayout(panelTratamientosLayout);
        panelTratamientosLayout.setHorizontalGroup(
            panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTratamientosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbdesc)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelTratamientosLayout.createSequentialGroup()
                        .addGroup(panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTratamientosLayout.createSequentialGroup()
                                .addComponent(lbFlt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtFiltroMed))
                            .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chkMedicacion)
                            .addComponent(btnMedicamento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEliminarMed, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTratamientosLayout.createSequentialGroup()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(btnFinDiag))
                    .addComponent(lbtrat)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTratamientosLayout.setVerticalGroup(
            panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTratamientosLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc)
                    .addComponent(lbtrat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                    .addComponent(jScrollPane9))
                .addGap(14, 14, 14)
                .addGroup(panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTratamientosLayout.createSequentialGroup()
                        .addGroup(panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFiltroMed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbFlt)
                            .addComponent(chkMedicacion))
                        .addGap(41, 41, 41)
                        .addComponent(btnMedicamento)
                        .addGap(7, 7, 7)
                        .addComponent(btnEliminarMed)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTratamientosLayout.createSequentialGroup()
                        .addGroup(panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane11, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelTratamientosLayout.createSequentialGroup()
                                .addGap(0, 32, Short.MAX_VALUE)
                                .addGroup(panelTratamientosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnFinDiag, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(26, 26, 26))))
        );

        cbVetDiagnosticos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbVetDiagnosticos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbVetDiagnosticosActionPerformed(evt);
            }
        });

        lbCitasVeterinario.setText("Citas para el veterinario:");

        javax.swing.GroupLayout panelDiagnosticosLayout = new javax.swing.GroupLayout(panelDiagnosticos);
        panelDiagnosticos.setLayout(panelDiagnosticosLayout);
        panelDiagnosticosLayout.setHorizontalGroup(
            panelDiagnosticosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDiagnosticosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDiagnosticosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelTratamientos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelDiagnosticosLayout.createSequentialGroup()
                        .addGroup(panelDiagnosticosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelDiagnosticosLayout.createSequentialGroup()
                                .addComponent(lbCitasVeterinario)
                                .addGap(23, 23, 23)
                                .addComponent(cbVetDiagnosticos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelDatosDiag, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelDiagnosticosLayout.setVerticalGroup(
            panelDiagnosticosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDiagnosticosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDiagnosticosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDiagnosticosLayout.createSequentialGroup()
                        .addGroup(panelDiagnosticosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbVetDiagnosticos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbCitasVeterinario))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane7))
                    .addComponent(panelDatosDiag, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelTratamientos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        dialogEditClientes.setResizable(false);
        dialogEditClientes.setSize(new java.awt.Dimension(575, 720));

        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        panelDatosFamiliar.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Familiar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        lbDireFami.setText("Dirección:");

        txtDireFami.setText(" ");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos Contacto"));

        lbMailFami.setText("E-mail:");

        lbTlfFami.setText("Teléfono:");

        txtTlfFami.setText(" ");

        txtMailFami.setText(" ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbTlfFami)
                    .addComponent(lbMailFami))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMailFami, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .addComponent(txtTlfFami))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbTlfFami)
                    .addComponent(txtTlfFami, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbMailFami)
                    .addComponent(txtMailFami, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos Personales"));

        lbDniCli.setText("Dni:");

        lbNombreFami.setText("Nombre:");

        txtNombreFami.setText(" ");

        cbDniFami.setEditable(true);
        cbDniFami.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDniFamiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDniCli)
                    .addComponent(lbNombreFami))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNombreFami)
                    .addComponent(cbDniFami, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDniCli)
                    .addComponent(cbDniFami, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNombreFami)
                    .addComponent(txtNombreFami, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelDatosFamiliarLayout = new javax.swing.GroupLayout(panelDatosFamiliar);
        panelDatosFamiliar.setLayout(panelDatosFamiliarLayout);
        panelDatosFamiliarLayout.setHorizontalGroup(
            panelDatosFamiliarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosFamiliarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosFamiliarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDatosFamiliarLayout.createSequentialGroup()
                        .addComponent(lbDireFami)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDireFami))
                    .addGroup(panelDatosFamiliarLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelDatosFamiliarLayout.setVerticalGroup(
            panelDatosFamiliarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosFamiliarLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(panelDatosFamiliarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosFamiliarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbDireFami)
                    .addComponent(txtDireFami, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelDatosMed.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Médicos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        lbPesoCli.setText("Peso:");

        panelVacunas.setBorder(javax.swing.BorderFactory.createTitledBorder("Vacunas"));

        cbVacuna1.setText("vac1");

        cbVacuna2.setText("vac2");

        cbVacuna3.setText("vac3");

        cbVacuna4.setText("vac4");

        cbVacuna5.setText("vac5");

        cbVacuna6.setText("vac6");

        javax.swing.GroupLayout panelVacunasLayout = new javax.swing.GroupLayout(panelVacunas);
        panelVacunas.setLayout(panelVacunasLayout);
        panelVacunasLayout.setHorizontalGroup(
            panelVacunasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVacunasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelVacunasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelVacunasLayout.createSequentialGroup()
                        .addComponent(cbVacuna1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbVacuna3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbVacuna5))
                    .addGroup(panelVacunasLayout.createSequentialGroup()
                        .addComponent(cbVacuna2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbVacuna4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbVacuna6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelVacunasLayout.setVerticalGroup(
            panelVacunasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVacunasLayout.createSequentialGroup()
                .addGroup(panelVacunasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbVacuna1)
                    .addComponent(cbVacuna3)
                    .addComponent(cbVacuna5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelVacunasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbVacuna2)
                    .addComponent(cbVacuna4)
                    .addComponent(cbVacuna6))
                .addContainerGap())
        );

        txtComentarioCli.setColumns(20);
        txtComentarioCli.setRows(5);
        jScrollPane1.setViewportView(txtComentarioCli);

        lbComentarioCli.setText("Comentarios:");

        lbFecha_nacAni.setText("Fecha Nac:");

        txtFechaCli.setText(" ");

        javax.swing.GroupLayout PanelDatosMedLayout = new javax.swing.GroupLayout(PanelDatosMed);
        PanelDatosMed.setLayout(PanelDatosMedLayout);
        PanelDatosMedLayout.setHorizontalGroup(
            PanelDatosMedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDatosMedLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelDatosMedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(PanelDatosMedLayout.createSequentialGroup()
                        .addGroup(PanelDatosMedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelDatosMedLayout.createSequentialGroup()
                                .addComponent(lbComentarioCli)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(PanelDatosMedLayout.createSequentialGroup()
                                .addGroup(PanelDatosMedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbFecha_nacAni)
                                    .addComponent(lbPesoCli))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PanelDatosMedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPesoCli)
                                    .addComponent(txtFechaCli))))
                        .addGap(10, 10, 10)
                        .addComponent(panelVacunas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        PanelDatosMedLayout.setVerticalGroup(
            PanelDatosMedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDatosMedLayout.createSequentialGroup()
                .addGroup(PanelDatosMedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelDatosMedLayout.createSequentialGroup()
                        .addComponent(panelVacunas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelDatosMedLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(PanelDatosMedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbFecha_nacAni)
                            .addComponent(txtFechaCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PanelDatosMedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbPesoCli)
                            .addComponent(txtPesoCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addComponent(lbComentarioCli)
                        .addGap(3, 3, 3)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelDatosAnimal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Animal", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        lbNombreAni.setText("Nombre:");

        jLabel3.setText("Sexo:");

        cbTipoAni.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Gato", "Perro", "Ave", "Roedor", "Conejo", "Pez", "Cerdo Vietnamita", "Animal de Granja", "Otro" }));
        cbTipoAni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTipoAniActionPerformed(evt);
            }
        });

        cbRazaAni.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lbTipoAni.setText("Tipo:");

        lbRazaAni.setText("Raza:");

        jLabel1.setText("id_chip:");

        rbgSexo.add(newRbHembra);
        newRbHembra.setSelected(true);
        newRbHembra.setText("Hembra");

        rbgSexo.add(newRbMacho);
        newRbMacho.setText("Macho");

        javax.swing.GroupLayout panelDatosAnimalLayout = new javax.swing.GroupLayout(panelDatosAnimal);
        panelDatosAnimal.setLayout(panelDatosAnimalLayout);
        panelDatosAnimalLayout.setHorizontalGroup(
            panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosAnimalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbNombreAni)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNombreCli, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                    .addComponent(txtChipidCli))
                .addGap(18, 18, 18)
                .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDatosAnimalLayout.createSequentialGroup()
                        .addComponent(lbTipoAni)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbTipoAni, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbRazaAni)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbRazaAni, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelDatosAnimalLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel3)
                        .addGap(24, 24, 24)
                        .addComponent(newRbHembra)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(newRbMacho)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        panelDatosAnimalLayout.setVerticalGroup(
            panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosAnimalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNombreAni)
                    .addComponent(cbRazaAni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTipoAni)
                    .addComponent(lbRazaAni)
                    .addComponent(cbTipoAni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(newRbHembra)
                        .addComponent(newRbMacho)
                        .addComponent(jLabel3))
                    .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtChipidCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dialogEditClientesLayout = new javax.swing.GroupLayout(dialogEditClientes.getContentPane());
        dialogEditClientes.getContentPane().setLayout(dialogEditClientesLayout);
        dialogEditClientesLayout.setHorizontalGroup(
            dialogEditClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogEditClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogEditClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDatosFamiliar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelDatosMed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelDatosAnimal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(dialogEditClientesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAceptar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar)
                        .addGap(4, 4, 4)))
                .addContainerGap())
        );
        dialogEditClientesLayout.setVerticalGroup(
            dialogEditClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogEditClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelDatosAnimal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(PanelDatosMed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelDatosFamiliar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(dialogEditClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAceptar)
                    .addComponent(btnCancelar))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        panelDatosFamiliar5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Familiar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos Contacto"));

        lbMailFami5.setText("E-mail:");

        lbTlfFami5.setText("Teléfono:");

        txtCitaTlfFami.setEditable(false);
        txtCitaTlfFami.setText(" ");

        txtCitaMailFami.setEditable(false);
        txtCitaMailFami.setText(" ");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbTlfFami5)
                    .addComponent(lbMailFami5))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCitaMailFami)
                    .addComponent(txtCitaTlfFami))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbTlfFami5)
                    .addComponent(txtCitaTlfFami, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbMailFami5)
                    .addComponent(txtCitaMailFami, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos Personales"));

        lbDniCli2.setText("Dni:");

        lbNombreFami2.setText("Nombre:");

        txtCitaNombreFami.setEditable(false);
        txtCitaNombreFami.setText(" ");

        cbCitaDni.setEditable(true);
        cbCitaDni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCitaDniActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDniCli2)
                    .addComponent(lbNombreFami2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCitaNombreFami)
                    .addComponent(cbCitaDni, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDniCli2)
                    .addComponent(cbCitaDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNombreFami2)
                    .addComponent(txtCitaNombreFami, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelDatosFamiliar5Layout = new javax.swing.GroupLayout(panelDatosFamiliar5);
        panelDatosFamiliar5.setLayout(panelDatosFamiliar5Layout);
        panelDatosFamiliar5Layout.setHorizontalGroup(
            panelDatosFamiliar5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosFamiliar5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelDatosFamiliar5Layout.setVerticalGroup(
            panelDatosFamiliar5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosFamiliar5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelDatosFamiliar5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        panelDatosCita.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Cita", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        panelDatosAniCita.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lbCitaNom.setText("Nombre:");

        lbCitaTipo.setText("Tipo:");

        lbCitaRaza.setText("Raza:");

        txtCitaAsunto.setColumns(20);
        txtCitaAsunto.setRows(5);
        txtCitaAsunto.setText("Escriba el motivo de la cita");
        jScrollPane5.setViewportView(txtCitaAsunto);

        lbCitaAsunto.setText("Asunto:");

        txtCitaTipo.setEditable(false);

        txtCitaRaza.setEditable(false);

        cbCitaNombreAni.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbCitaNombreAni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCitaNombreAniActionPerformed(evt);
            }
        });

        lbCitaId.setText("ID:");

        txtCitaId.setEditable(false);

        javax.swing.GroupLayout panelDatosAniCitaLayout = new javax.swing.GroupLayout(panelDatosAniCita);
        panelDatosAniCita.setLayout(panelDatosAniCitaLayout);
        panelDatosAniCitaLayout.setHorizontalGroup(
            panelDatosAniCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosAniCitaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosAniCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                    .addGroup(panelDatosAniCitaLayout.createSequentialGroup()
                        .addComponent(lbCitaAsunto)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelDatosAniCitaLayout.createSequentialGroup()
                        .addGroup(panelDatosAniCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbCitaNom)
                            .addComponent(lbCitaId))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelDatosAniCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCitaId)
                            .addComponent(cbCitaNombreAni, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(panelDatosAniCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbCitaTipo)
                            .addComponent(lbCitaRaza))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelDatosAniCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCitaRaza)
                            .addComponent(txtCitaTipo))))
                .addContainerGap())
        );
        panelDatosAniCitaLayout.setVerticalGroup(
            panelDatosAniCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosAniCitaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelDatosAniCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCitaNom)
                    .addComponent(lbCitaTipo)
                    .addComponent(txtCitaTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbCitaNombreAni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelDatosAniCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCitaRaza)
                    .addComponent(txtCitaRaza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCitaId)
                    .addComponent(txtCitaId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lbCitaAsunto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        cbCitaDia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbCitaDia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCitaDiaActionPerformed(evt);
            }
        });

        cbCitaHora.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbCitaHora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCitaHoraActionPerformed(evt);
            }
        });

        lbResumenCita.setText("Resumen Cita:");

        cbCitaMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));
        cbCitaMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCitaMesActionPerformed(evt);
            }
        });

        txtResumenCita.setEditable(false);
        txtResumenCita.setColumns(20);
        txtResumenCita.setRows(5);
        jScrollPane4.setViewportView(txtResumenCita);

        jLabel12.setText("Día:");

        jLabel13.setText("Hora:");

        cbVetCita.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lbVetCita.setText("Veterinario:");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(87, 87, 87))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cbCitaDia, 0, 232, Short.MAX_VALUE)
                                    .addGroup(jPanel17Layout.createSequentialGroup()
                                        .addComponent(lbVetCita)
                                        .addGap(18, 18, 18)
                                        .addComponent(cbVetCita, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(cbCitaHora, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(lbResumenCita)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cbCitaMes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbCitaMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbVetCita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbVetCita))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbCitaDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbCitaHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lbResumenCita)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelDatosCitaLayout = new javax.swing.GroupLayout(panelDatosCita);
        panelDatosCita.setLayout(panelDatosCitaLayout);
        panelDatosCitaLayout.setHorizontalGroup(
            panelDatosCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosCitaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelDatosAniCita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelDatosCitaLayout.setVerticalGroup(
            panelDatosCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosCitaLayout.createSequentialGroup()
                .addGroup(panelDatosCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelDatosAniCita, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        btnCitaAceptar.setText("Aceptar");
        btnCitaAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCitaAceptarActionPerformed(evt);
            }
        });

        btnCitaCancel.setText("Cancelar");
        btnCitaCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCitaCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dialogEditCitasLayout = new javax.swing.GroupLayout(dialogEditCitas.getContentPane());
        dialogEditCitas.getContentPane().setLayout(dialogEditCitasLayout);
        dialogEditCitasLayout.setHorizontalGroup(
            dialogEditCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogEditCitasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogEditCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDatosFamiliar5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelDatosCita, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(dialogEditCitasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCitaAceptar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCitaCancel)
                .addGap(29, 29, 29))
        );
        dialogEditCitasLayout.setVerticalGroup(
            dialogEditCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogEditCitasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelDatosFamiliar5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDatosCita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogEditCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCitaCancel)
                    .addComponent(btnCitaAceptar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dialogFichaAnimal.setMinimumSize(new java.awt.Dimension(532, 520));
        dialogFichaAnimal.setModal(true);
        dialogFichaAnimal.setResizable(false);

        lbFotoPerfil.setBackground(new java.awt.Color(255, 255, 255));
        lbFotoPerfil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/placeHolder.png"))); // NOI18N
        lbFotoPerfil.setOpaque(true);

        panelDatosFichaCli.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos básicos"));

        lbID.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbID.setText("ID:");

        lbNomb.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbNomb.setText("Nombre:");

        lbEspecie.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbEspecie.setText("Especie:");

        lbRaza.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbRaza.setText("Raza:");

        lbSexo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbSexo.setText("Sexo:");

        lbFechaNac.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbFechaNac.setText("Fecha de nacimiento:");

        javax.swing.GroupLayout panelDatosFichaCliLayout = new javax.swing.GroupLayout(panelDatosFichaCli);
        panelDatosFichaCli.setLayout(panelDatosFichaCliLayout);
        panelDatosFichaCliLayout.setHorizontalGroup(
            panelDatosFichaCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosFichaCliLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosFichaCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbFechaNac)
                    .addGroup(panelDatosFichaCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lbID, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lbNomb))
                    .addComponent(lbEspecie)
                    .addComponent(lbRaza)
                    .addComponent(lbSexo))
                .addGap(18, 18, 18)
                .addGroup(panelDatosFichaCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txID, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(txNomb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txEspecie, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txRaza, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txSexo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txFechaNac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDatosFichaCliLayout.setVerticalGroup(
            panelDatosFichaCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosFichaCliLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosFichaCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbID)
                    .addComponent(txID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosFichaCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txNomb, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbNomb))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosFichaCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txEspecie, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEspecie))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosFichaCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txRaza, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbRaza))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosFichaCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txSexo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbSexo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosFichaCliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txFechaNac, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbFechaNac))
                .addGap(22, 22, 22))
        );

        lbID.getAccessibleContext().setAccessibleName("lbID");
        lbID.getAccessibleContext().setAccessibleDescription("");
        lbNomb.getAccessibleContext().setAccessibleName("lbNom");
        lbEspecie.getAccessibleContext().setAccessibleName("lbEspecie");
        lbRaza.getAccessibleContext().setAccessibleName("lbRaza");

        panelDatosClinicos.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos clínicos"));

        lbPeso.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbPeso.setText("Ultimo peso:");

        lbVacunas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbVacunas.setText("Vacunas:");

        lbComentarios.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbComentarios.setText("Comentarios:");

        txComentario.setEditable(false);
        txComentario.setColumns(20);
        txComentario.setRows(5);
        jScrollPane10.setViewportView(txComentario);

        javax.swing.GroupLayout panelDatosClinicosLayout = new javax.swing.GroupLayout(panelDatosClinicos);
        panelDatosClinicos.setLayout(panelDatosClinicosLayout);
        panelDatosClinicosLayout.setHorizontalGroup(
            panelDatosClinicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosClinicosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosClinicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                    .addGroup(panelDatosClinicosLayout.createSequentialGroup()
                        .addGroup(panelDatosClinicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbComentarios)
                            .addGroup(panelDatosClinicosLayout.createSequentialGroup()
                                .addComponent(lbPeso)
                                .addGap(18, 18, 18)
                                .addComponent(txPeso))
                            .addGroup(panelDatosClinicosLayout.createSequentialGroup()
                                .addComponent(lbVacunas)
                                .addGap(40, 40, 40)
                                .addComponent(txVacunas)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelDatosClinicosLayout.setVerticalGroup(
            panelDatosClinicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosClinicosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosClinicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbPeso)
                    .addComponent(txPeso))
                .addGap(17, 17, 17)
                .addGroup(panelDatosClinicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbVacunas)
                    .addComponent(txVacunas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbComentarios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );

        javax.swing.GroupLayout dialogFichaAnimalLayout = new javax.swing.GroupLayout(dialogFichaAnimal.getContentPane());
        dialogFichaAnimal.getContentPane().setLayout(dialogFichaAnimalLayout);
        dialogFichaAnimalLayout.setHorizontalGroup(
            dialogFichaAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogFichaAnimalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogFichaAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDatosClinicos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(dialogFichaAnimalLayout.createSequentialGroup()
                        .addComponent(lbFotoPerfil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelDatosFichaCli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        dialogFichaAnimalLayout.setVerticalGroup(
            dialogFichaAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogFichaAnimalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogFichaAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelDatosFichaCli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbFotoPerfil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelDatosClinicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dialogEditVet.setResizable(false);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Veterinario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        lbVetDni.setText("DNI:");

        lbVetEmail.setText("E_Mail:");

        lbNum_Licencia.setText("Num_Licencia:");

        btnVetCancelar.setText("Cancelar");
        btnVetCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVetCancelarActionPerformed(evt);
            }
        });

        lbVetNombre.setText("Nombre:");

        btnVetAceptar.setText("Aceptar");
        btnVetAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVetAceptarActionPerformed(evt);
            }
        });

        lbVetTlf.setText("Teléfono:");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lbVetDni)
                                    .addComponent(lbVetEmail)
                                    .addComponent(lbVetTlf)
                                    .addComponent(lbVetNombre)))
                            .addComponent(lbNum_Licencia))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtVetTlf, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVetDni, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNumLicencia, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVetEmail)
                            .addComponent(txtVetNombre)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addContainerGap(194, Short.MAX_VALUE)
                        .addComponent(btnVetAceptar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnVetCancelar)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVetNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbVetNombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVetTlf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbVetTlf))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVetDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbVetDni))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVetEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbVetEmail))
                .addGap(15, 15, 15)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNum_Licencia)
                    .addComponent(txtNumLicencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVetCancelar)
                    .addComponent(btnVetAceptar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dialogEditVetLayout = new javax.swing.GroupLayout(dialogEditVet.getContentPane());
        dialogEditVet.getContentPane().setLayout(dialogEditVetLayout);
        dialogEditVetLayout.setHorizontalGroup(
            dialogEditVetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogEditVetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dialogEditVetLayout.setVerticalGroup(
            dialogEditVetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        icContacto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/contacto.png"))); // NOI18N

        lbDNI.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbDNI.setText("DNI:");

        lbNombre.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbNombre.setText("Nombre:");

        lbTel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbTel.setText("Teléfono:");

        lbEmail.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbEmail.setText("Email:");

        lbDir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbDir.setText("Dirección");

        javax.swing.GroupLayout dialogContactoLayout = new javax.swing.GroupLayout(dialogContacto.getContentPane());
        dialogContacto.getContentPane().setLayout(dialogContactoLayout);
        dialogContactoLayout.setHorizontalGroup(
            dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogContactoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(icContacto)
                .addGap(18, 18, 18)
                .addGroup(dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogContactoLayout.createSequentialGroup()
                        .addGroup(dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbDir)
                            .addComponent(lbEmail))
                        .addGap(18, 18, 18)
                        .addGroup(dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txEmail)
                            .addComponent(txDir)))
                    .addGroup(dialogContactoLayout.createSequentialGroup()
                        .addGroup(dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbTel)
                            .addComponent(lbNombre)
                            .addComponent(lbDNI))
                        .addGap(18, 18, 18)
                        .addGroup(dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txDNI)
                            .addComponent(txNombre)
                            .addComponent(txTel))))
                .addContainerGap(210, Short.MAX_VALUE))
        );
        dialogContactoLayout.setVerticalGroup(
            dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogContactoLayout.createSequentialGroup()
                .addGroup(dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogContactoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(icContacto))
                    .addGroup(dialogContactoLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbDNI)
                            .addComponent(txDNI))
                        .addGap(18, 18, 18)
                        .addGroup(dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbNombre)
                            .addComponent(txNombre))
                        .addGap(18, 18, 18)
                        .addGroup(dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbTel)
                            .addComponent(txTel))
                        .addGap(18, 18, 18)
                        .addGroup(dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbEmail)
                            .addComponent(txEmail))
                        .addGap(18, 18, 18)
                        .addGroup(dialogContactoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbDir)
                            .addComponent(txDir))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbVet.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbVet.setText("Veterinario:");

        lbCli.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbCli.setText("Animal:");

        lbHora.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbHora.setText("Hora:");

        lbFam.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbFam.setText("Familiar:");

        lbAsunto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbAsunto.setText("Asunto:");

        lbTelFam.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbTelFam.setText("Teléfono:");

        javax.swing.GroupLayout dialogDetallesCitaLayout = new javax.swing.GroupLayout(dialogDetallesCita.getContentPane());
        dialogDetallesCita.getContentPane().setLayout(dialogDetallesCitaLayout);
        dialogDetallesCitaLayout.setHorizontalGroup(
            dialogDetallesCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogDetallesCitaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogDetallesCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogDetallesCitaLayout.createSequentialGroup()
                        .addGroup(dialogDetallesCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbVet)
                            .addComponent(lbCli)
                            .addComponent(lbFam)
                            .addComponent(lbHora)
                            .addComponent(lbAsunto))
                        .addGap(18, 18, 18)
                        .addGroup(dialogDetallesCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txAsunto)
                            .addComponent(txHora)
                            .addComponent(txFam)
                            .addComponent(txCli)
                            .addComponent(txVet)
                            .addComponent(txTelFam)))
                    .addComponent(lbTelFam))
                .addContainerGap(406, Short.MAX_VALUE))
        );
        dialogDetallesCitaLayout.setVerticalGroup(
            dialogDetallesCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogDetallesCitaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogDetallesCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbVet)
                    .addComponent(txVet))
                .addGap(18, 18, 18)
                .addGroup(dialogDetallesCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCli)
                    .addComponent(txCli))
                .addGap(18, 18, 18)
                .addGroup(dialogDetallesCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbFam)
                    .addComponent(txFam))
                .addGap(18, 18, 18)
                .addGroup(dialogDetallesCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbTelFam)
                    .addComponent(txTelFam))
                .addGap(18, 18, 18)
                .addGroup(dialogDetallesCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbHora)
                    .addComponent(txHora))
                .addGap(18, 18, 18)
                .addGroup(dialogDetallesCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbAsunto)
                    .addComponent(txAsunto))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        comboBox5Ultimos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBox5UltimosActionPerformed(evt);
            }
        });

        panelDetalles.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalles"));

        lbDiagAnimal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbDiagAnimal.setText("Animal:");

        lbDiagDiagnostico.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbDiagDiagnostico.setText("Diagnósitico:");

        txaDiagnostico.setEditable(false);
        txaDiagnostico.setColumns(20);
        txaDiagnostico.setRows(5);
        jScrollPane13.setViewportView(txaDiagnostico);

        lbTipTratamiento.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbTipTratamiento.setText("Tratamiento:");

        txaTratamiento.setEditable(false);
        txaTratamiento.setColumns(20);
        txaTratamiento.setRows(5);
        jScrollPane14.setViewportView(txaTratamiento);

        lbContacto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbContacto.setText("Contacto:");

        lbMedicacion.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbMedicacion.setText("Medicación:");

        javax.swing.GroupLayout panelDetallesLayout = new javax.swing.GroupLayout(panelDetalles);
        panelDetalles.setLayout(panelDetallesLayout);
        panelDetallesLayout.setHorizontalGroup(
            panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetallesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                    .addComponent(jScrollPane14)
                    .addGroup(panelDetallesLayout.createSequentialGroup()
                        .addGroup(panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbDiagDiagnostico)
                            .addComponent(lbTipTratamiento)
                            .addGroup(panelDetallesLayout.createSequentialGroup()
                                .addComponent(lbDiagAnimal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txaNombre))
                            .addGroup(panelDetallesLayout.createSequentialGroup()
                                .addComponent(lbMedicacion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txaMedicacion))
                            .addGroup(panelDetallesLayout.createSequentialGroup()
                                .addComponent(lbContacto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txaContacto)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelDetallesLayout.setVerticalGroup(
            panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetallesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbDiagAnimal)
                    .addComponent(txaNombre))
                .addGap(18, 18, 18)
                .addComponent(lbDiagDiagnostico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbTipTratamiento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbMedicacion)
                    .addComponent(txaMedicacion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbContacto)
                    .addComponent(txaContacto))
                .addContainerGap())
        );

        jLabel17.setText("Diagnóstico:");

        javax.swing.GroupLayout dialogUltimosDiagnosticosLayout = new javax.swing.GroupLayout(dialogUltimosDiagnosticos.getContentPane());
        dialogUltimosDiagnosticos.getContentPane().setLayout(dialogUltimosDiagnosticosLayout);
        dialogUltimosDiagnosticosLayout.setHorizontalGroup(
            dialogUltimosDiagnosticosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogUltimosDiagnosticosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogUltimosDiagnosticosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogUltimosDiagnosticosLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboBox5Ultimos, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(dialogUltimosDiagnosticosLayout.createSequentialGroup()
                        .addComponent(panelDetalles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        dialogUltimosDiagnosticosLayout.setVerticalGroup(
            dialogUltimosDiagnosticosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogUltimosDiagnosticosLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(dialogUltimosDiagnosticosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBox5Ultimos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelDetalles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelConfigDB.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Base de Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        lbGestor.setText("Gestor:");

        cbGestor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hibernate", "Neodatis" }));
        cbGestor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbGestorActionPerformed(evt);
            }
        });

        lbURL.setText("URL:");

        lbUsuario.setText("Usuario");

        lbContraseña.setText("Contraseña:");

        lbIconoGestor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/hibernate.png"))); // NOI18N

        javax.swing.GroupLayout panelConfigDBLayout = new javax.swing.GroupLayout(panelConfigDB);
        panelConfigDB.setLayout(panelConfigDBLayout);
        panelConfigDBLayout.setHorizontalGroup(
            panelConfigDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelConfigDBLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelConfigDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbURL)
                    .addComponent(lbGestor)
                    .addComponent(lbUsuario)
                    .addComponent(lbContraseña))
                .addGap(18, 18, 18)
                .addGroup(panelConfigDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbGestor, 0, 100, Short.MAX_VALUE)
                    .addComponent(txURL)
                    .addComponent(txContraseña)
                    .addComponent(txUsuario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbIconoGestor, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelConfigDBLayout.setVerticalGroup(
            panelConfigDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelConfigDBLayout.createSequentialGroup()
                .addGroup(panelConfigDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelConfigDBLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelConfigDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbGestor)
                            .addComponent(cbGestor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelConfigDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbURL)
                            .addComponent(txURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelConfigDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbUsuario))
                        .addGap(18, 18, 18)
                        .addGroup(panelConfigDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbContraseña)))
                    .addComponent(lbIconoGestor, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        btCancelarConfig.setText("Cancelar");
        btCancelarConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelarConfigActionPerformed(evt);
            }
        });

        btAceptarConfig.setText("Aceptar");
        btAceptarConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAceptarConfigActionPerformed(evt);
            }
        });

        checkDemo.setText("Cargar datos de demostración");

        javax.swing.GroupLayout dialogConfiguracionLayout = new javax.swing.GroupLayout(dialogConfiguracion.getContentPane());
        dialogConfiguracion.getContentPane().setLayout(dialogConfiguracionLayout);
        dialogConfiguracionLayout.setHorizontalGroup(
            dialogConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogConfiguracionLayout.createSequentialGroup()
                .addGroup(dialogConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(dialogConfiguracionLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelConfigDB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(dialogConfiguracionLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(checkDemo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btAceptarConfig)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btCancelarConfig)))
                .addContainerGap())
        );
        dialogConfiguracionLayout.setVerticalGroup(
            dialogConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogConfiguracionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelConfigDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(dialogConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCancelarConfig)
                    .addComponent(btAceptarConfig)
                    .addComponent(checkDemo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelInfo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        mypets.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        mypets.setText("MYPETS");

        lbCreado.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbCreado.setText("Creado por:");

        lbDavid.setText("David García Rubio");

        lbMiguel.setText("Miguel Pereira Fernández");

        lbSaul.setText("Saúl Pérez Benéitez");

        lbModulo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbModulo.setText("Modulo:");

        lbAAD.setText("Acceso a Datos");

        lbCiclo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbCiclo.setText("Ciclo:");

        lbDAM.setText("Desarrollo de aplicaciones multiplataforma");

        lbAño.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbAño.setText("Año:");

        lb16_17.setText("2016-2017");

        javax.swing.GroupLayout panelInfoLayout = new javax.swing.GroupLayout(panelInfo);
        panelInfo.setLayout(panelInfoLayout);
        panelInfoLayout.setHorizontalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mypets)
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbCreado)
                            .addComponent(lbModulo)
                            .addComponent(lbCiclo)
                            .addComponent(lbAño))
                        .addGap(18, 18, 18)
                        .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lb16_17)
                            .addComponent(lbDAM)
                            .addComponent(lbAAD)
                            .addComponent(lbMiguel)
                            .addComponent(lbDavid)
                            .addComponent(lbSaul))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelInfoLayout.setVerticalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mypets)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCreado)
                    .addComponent(lbDavid))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbMiguel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbSaul)
                .addGap(18, 18, 18)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbModulo)
                    .addComponent(lbAAD))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCiclo)
                    .addComponent(lbDAM))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbAño)
                    .addComponent(lb16_17))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dialogInfoLayout = new javax.swing.GroupLayout(dialogInfo.getContentPane());
        dialogInfo.getContentPane().setLayout(dialogInfoLayout);
        dialogInfoLayout.setHorizontalGroup(
            dialogInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dialogInfoLayout.setVerticalGroup(
            dialogInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        dialogPremium.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dialogPremiumWindowClosing(evt);
            }
        });

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/gold.png"))); // NOI18N

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Esta función solo está disponible en la versión premium!");

        jLabel19.setForeground(new java.awt.Color(0, 0, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("http://www.mypets.com/premium");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Precio: 400€/año");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel22)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dialogPremiumLayout = new javax.swing.GroupLayout(dialogPremium.getContentPane());
        dialogPremium.getContentPane().setLayout(dialogPremiumLayout);
        dialogPremiumLayout.setHorizontalGroup(
            dialogPremiumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogPremiumLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogPremiumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogPremiumLayout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dialogPremiumLayout.setVerticalGroup(
            dialogPremiumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogPremiumLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        lbLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/vet-logo.gif"))); // NOI18N

        btnPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/playPeque.png"))); // NOI18N
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });

        btnConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/ajustesPqe.png"))); // NOI18N
        btnConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfigActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pbLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnConfig)
                            .addComponent(btnPlay)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(pbLoading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clearClientes () {
        
        txtNombreCli.setText("");
        txtChipidCli.setText("");
        txtFechaCli.setText("");
        txtComentarioCli.setText("");
        txtNombreFami.setText("");
        txtTlfFami.setText("");
        txtMailFami.setText("");
        txtDireFami.setText("");
        txtPesoCli.setText("");
        cbVacuna1.setSelected(false);
        cbVacuna2.setSelected(false);
        cbVacuna3.setSelected(false);
        cbVacuna4.setSelected(false);
        cbVacuna5.setSelected(false);
        cbVacuna6.setSelected(false);
        
        cbTipoAni.removeAllItems();
        for (String tipo : tipos) {
            cbTipoAni.addItem(tipo);
        }
        
        cbRazaAni.removeAllItems();
        for (String rGato : razasGato) {
            cbRazaAni.addItem(rGato);
        }
        Component[] vacunas= panelVacunas.getComponents();
        for (int i=0;i<vacunasGato.length;i++) {
            ((JCheckBox)vacunas[i]).setText(vacunasGato[i]);
        }
        
        cbDniFami.removeAllItems();
        cbTipoAni.setSelectedIndex(0);
        
        newRbHembra.setSelected(true);
    }
    
    
    private void btnNuevoCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoCliActionPerformed


        clearClientes();
        dialogEditClientes.setLocationRelativeTo(frameClientes);
        dialogEditClientes.setVisible(true);
        dialogEditClientes.setModal(true);
        
    }//GEN-LAST:event_btnNuevoCliActionPerformed

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed

        String nombreAni, tipo, raza, comentario;
        Date fecha_nac;
        float peso;
        char sexo;
        

        nombreAni=txtNombreCli.getText();
        tipo=cbTipoAni.getSelectedItem().toString();
        raza=cbRazaAni.getSelectedItem().toString();
        fecha_nac=new Date();

        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

        try{
        Date date = parser.parse(txtFechaCli.getText());
        }catch (ParseException e){ 
            System.out.println("------------ERROR DE FORMATO EN LA FECHA!!!   yyy-MM-dd ---------------------------------");
        }
        peso=Float.parseFloat( txtPesoCli.getText() );
        comentario=txtComentarioCli.getText();

        if (newRbHembra.isSelected())
            sexo='H';
        else
            sexo='M';

        String dni= cbDniFami.getSelectedItem().toString();

        String Nombre = txtNombreFami.getText();

        String Telefono = txtTlfFami.getText();

        String mail = txtMailFami.getText();

        C_Familiar familiar= new C_Familiar (dni, Nombre, Telefono, mail, Telefono);

        C_Animal animal=new C_Animal(nombreAni, tipo, raza, sexo, fecha_nac, peso, comentario, familiar);
        
        LinkedList listaVacunas=new LinkedList<>();
        
        if(cbVacuna1.isSelected())
            listaVacunas.add(cbVacuna1.getText());
        if(cbVacuna2.isSelected())
            listaVacunas.add(cbVacuna2.getText());
        if(cbVacuna3.isSelected())
            listaVacunas.add(cbVacuna3.getText());
        if(cbVacuna4.isSelected())
            listaVacunas.add(cbVacuna4.getText());
        if(cbVacuna5.isSelected())
            listaVacunas.add(cbVacuna5.getText());
        if(cbVacuna6.isSelected())
            listaVacunas.add(cbVacuna6.getText());
        
        if(update)
        { 
            Update.updateAnimal(animal,idUpdate,listaVacunas);
            update=false;
            
        }
        else
            Guardar.guardarAnimal(animal, listaVacunas);
        
        modeloClientes.setRowCount(0);
        cargarAnimales();

        dialogEditClientes.dispose();
        
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void clearDialogCitas() {
        
        cbCitaDni.removeAllItems();
        cbCitaDni.setEditable(true);
        cbCitaDni.setEnabled(true);
        txtCitaNombreFami.setText("         ");
        txtCitaTipo.setText("");
        txtCitaTlfFami.setText("");
        txtCitaMailFami.setText("");
        cbCitaNombreAni.removeAllItems();
        cbCitaNombreAni.setEnabled(true);
        
        
        txtCitaAsunto.setText("");
        txtCitaRaza.setText("");
        txtCitaId.setText("");


        cbVetCita.removeAllItems();
        Consultas.cargarVetCombo(cbVetCita); 
        txtResumenCita.setText("");
        cargarCitasLibres();
                
    }
    private void btnNuevaCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaCitaActionPerformed
        
        
        clearDialogCitas();
        dialogEditCitas.setLocationRelativeTo(frameClientes);
        dialogEditCitas.setVisible(true);
        dialogEditCitas.setModal(true);
        dialogEditCitas.setSize(800, 650);
        cbCitaNombreAni.setEnabled(false);

        cargarCitasLibres();
        Consultas.cargarVetCombo(cbVetCita);   
        
    }//GEN-LAST:event_btnNuevaCitaActionPerformed

    private static void configurarCalendario() {
        //configuracion del renderer
        DefaultTableCellRenderer myRenderer = new DefaultTableCellRenderer();
        myRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        myRenderer.setFont(new Font("Tahoma", Font.BOLD, 18));////<<<<ESTO NO VA!!! ARREGLAAAAAAAAAAR!!!!!

        
        
        //fin renderer
        tablaCitas.setCellSelectionEnabled(true);
        tablaCitas.setModel(modeloCitas);
        tablaCitas.setRowHeight(40);
        
        //tablaCitas.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 13));
        tablaCitas.getTableHeader().setDefaultRenderer(myRenderer);
        tablaCitas.getColumnModel().getColumn(0).setCellRenderer( myRenderer );
    }
    private void btnCitasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCitasActionPerformed
        
        cambiarPanel(panelCitas);
        cargarCitas();
        configurarCalendario();
        cambiarFechasCitas();
  
    }//GEN-LAST:event_btnCitasActionPerformed

    
    private void btnFacturasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturasActionPerformed
        cambiarPanel(panelFacturas);
        dialogPremium.setModal(true);
        dialogPremium.setSize(425,352);
        dialogPremium.setLocationRelativeTo(panelFacturas);
        dialogPremium.setVisible(true);
        Date today=new Date();
        
        txtFechaFactura.setText(today.toString());
        
        int max=Consultas.numFactura();
        
        
        
        
    }//GEN-LAST:event_btnFacturasActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        cambiarPanel(panelClientes);
    }//GEN-LAST:event_btnClientesActionPerformed

    private void btnVeterinariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVeterinariosActionPerformed
        cambiarPanel(panelVeterinarios);
        
        
        String[] columnasVet={"id", "Nombre", "Num_Licencia"};
        modeloVet=new DefaultTableModel(columnasVet,0);
        tablaVeterinarios.setModel(modeloVet);
        
        try{
            
            Iterator veterinarios = Consultas.cargarVeterinarios();
            
            while(veterinarios.hasNext())
            {
                C_Veterinario vet=(C_Veterinario)veterinarios.next();
                
                int id=vet.getId();
                String nombre=vet.getNombre();
                String licencia=vet.getNumLicencia();
                
                Object[] fila= {id, nombre,licencia};
                modeloVet.addRow(fila);
            }

        }catch (Exception e) {
            
            System.out.println(e.getMessage());
        }
        
        
        
    }//GEN-LAST:event_btnVeterinariosActionPerformed

    private void btnInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarioActionPerformed
        cambiarPanel(panelInventario);
        dialogPremium.setModal(true);
        dialogPremium.setSize(425,352);
        dialogPremium.setLocationRelativeTo(panelInventario);
        dialogPremium.setVisible(true);
    }//GEN-LAST:event_btnInventarioActionPerformed

    private void btnEditarCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarCliActionPerformed
        //PANEL CLIENTES BOTON EDITAR CLIENTE
        clearClientes();
        if(tablaClientes.getSelectedRowCount()>0)
        {
            int id=(int)modeloClientes.getValueAt(tablaClientes.getSelectedRow(), 0);
            System.out.println("tengo el id:"+id);
            C_Animal animal = Consultas.recuperarAnimalPorId(id);
            System.out.println("tengo el id:"+id+" q es de "+animal.getNombre());
            
            
            txtNombreCli.setText(animal.getNombre());
            txtChipidCli.setText(animal.getId_chip());
            //cbTipoAni.setSelectedItem(animal.getTipo());
            cbRazaAni.setSelectedItem(animal.getRaza());
            
            txtFechaCli.setText(animal.getFecha_nac().toString());
            txtPesoCli.setText(""+animal.getPeso());
            txtComentarioCli.setText(animal.getComentario());
            
            //--tipo
            
            int tipo=0;
            if(animal.getTipo().compareToIgnoreCase("gato")==0)
                tipo=0;
            else if(animal.getTipo().compareToIgnoreCase("perro")==0)
                tipo=1;
            else if(animal.getTipo().compareToIgnoreCase("Pajaro")==0)
                tipo=2;
            else if(animal.getTipo().compareToIgnoreCase("pez")==0)
                tipo=3;
            else if(animal.getTipo().compareToIgnoreCase("roedor")==0)
                tipo=4;
            else if(animal.getTipo().compareToIgnoreCase("vaca")==0)
                tipo=5;
            else if(animal.getTipo().compareToIgnoreCase("cerdo")==0)
                tipo=6;
            else if(animal.getTipo().compareToIgnoreCase("caballo")==0)
                tipo=7;
            
            System.out.println("el tipo es "+tipo);
            cargarComboTipos(tipo);
            cbTipoAni.setSelectedIndex(tipo);
            
            //--vacunas
            
            Iterator vacunas=animal.getVacunas().iterator();
            
            while(vacunas.hasNext())
            {
                C_Medicamento vacuna=(C_Medicamento)vacunas.next();
                System.out.println("tengo una vacuna");
                if(cbVacuna1.getText().compareToIgnoreCase(vacuna.getNombre())==0)
                    cbVacuna1.setSelected(true);
                if(cbVacuna2.getText().compareToIgnoreCase(vacuna.getNombre())==0)
                    cbVacuna2.setSelected(true);
                if(cbVacuna3.getText().compareToIgnoreCase(vacuna.getNombre())==0)
                    cbVacuna3.setSelected(true);
                if(cbVacuna4.getText().compareToIgnoreCase(vacuna.getNombre())==0)
                    cbVacuna4.setSelected(true);
                if(cbVacuna5.getText().compareToIgnoreCase(vacuna.getNombre())==0)
                    cbVacuna5.setSelected(true);
                if(cbVacuna6.getText().compareToIgnoreCase(vacuna.getNombre())==0)
                    cbVacuna6.setSelected(true);
            }

            //--familiar--
            
            cbDniFami.setSelectedItem(animal.getFamiliar().getDni());
            txtNombreFami.setText(animal.getFamiliar().getNombre());
            txtTlfFami.setText(animal.getFamiliar().getTelefono());
            txtMailFami.setText(animal.getFamiliar().getEmail());
            txtDireFami.setText( ((C_Familiar)animal.getFamiliar()).getDireccion());
            
            dialogEditClientes.setLocationRelativeTo(frameClientes);
            dialogEditClientes.setVisible(true);
            dialogEditClientes.setModal(true);
            //cargarFamiliares();
            
            update=true;
            idUpdate=id;
        
        }
        
    }//GEN-LAST:event_btnEditarCliActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        
        dialogEditClientes.dispose();
        update=false;
        //dialogEditClientes.setVisible(false);

                
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void cbDniFamiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDniFamiActionPerformed
        //DIALOG_CLIENTES COMBODNI CLICK
        try{
        String dni= cbDniFami.getSelectedItem().toString();
        
        C_Persona familiar= Consultas.recuperarUnaPersonaPordni(dni);
        
        if(familiar != null){
            txtTlfFami.setText(familiar.getTelefono());
            txtDireFami.setText(((C_Familiar)familiar).getDireccion());
            txtMailFami.setText(familiar.getEmail());
            txtNombreFami.setText(familiar.getNombre());
            
        }
      }catch(Exception e){}
        
    }//GEN-LAST:event_cbDniFamiActionPerformed

    private void btnEliminarCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarCliActionPerformed
        //PANEL CLIENTES BOTON ELIMINAR CLIENTE
        if(tablaClientes.getSelectedRowCount()>0)
        {
            int id=(int)modeloClientes.getValueAt(tablaClientes.getSelectedRow(), 0);    
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (frameClientes, "¿Esta Seguro que desea eliminar este registro?","Warning",dialogButton);
            
            if(dialogResult == JOptionPane.YES_OPTION){
            
                Eliminar.EliminarAnimal(id);
                
                modeloClientes.removeRow(tablaClientes.getSelectedRow()); 
            }
        }
    }//GEN-LAST:event_btnEliminarCliActionPerformed

    private void cbCitaDniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCitaDniActionPerformed
        
        
        try{
           
            String dni= cbCitaDni.getSelectedItem().toString();

            
            C_Persona familiar= Consultas.recuperarUnaPersonaPordni(dni);

            if(familiar != null){

                txtCitaTlfFami.setText(familiar.getTelefono());
                txtCitaMailFami.setText(familiar.getEmail());
                txtCitaNombreFami.setText(familiar.getNombre());
                cbCitaNombreAni.setEnabled(true);
                cbCitaNombreAni.removeAllItems();
                Iterator animales=Consultas.recuperarAnimalesPorIdFamiliar(familiar.getId());
                while(animales.hasNext())
                {
                    C_Animal animal=(C_Animal)animales.next();
                    cbCitaNombreAni.addItem(animal.getNombre());
                }
            
        }
      }catch(Exception e){}
        
        
        
    }//GEN-LAST:event_cbCitaDniActionPerformed

    private void btnCitaCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCitaCliActionPerformed
        //PANEL CLIENTES BOTON NUEVA CITA
        
        clearDialogCitas();
        if(tablaClientes.getSelectedRowCount()>0)
        {
            
            int id=(int)modeloClientes.getValueAt(tablaClientes.getSelectedRow(), 0);
            
            C_Animal animal = Consultas.recuperarAnimalPorId(id);
            

            cbCitaDni.addItem(animal.getFamiliar().getDni());
            txtCitaNombreFami.setText(animal.getFamiliar().getNombre()); txtCitaNombreFami.setEditable(false);
            txtCitaTlfFami.setText(animal.getFamiliar().getTelefono()); txtCitaTlfFami.setEditable(false);
            cbCitaDni.setSelectedItem(animal.getFamiliar().getDni()); cbCitaDni.setEditable(false);
            cbCitaNombreAni.removeAllItems(); cbCitaNombreAni.setEditable(false);
            cbCitaNombreAni.addItem(animal.getNombre()); cbCitaNombreAni.setEditable(false);
            txtCitaId.setText(String.valueOf(animal.getId())); txtCitaId.setEditable(false);

            txtCitaMailFami.setText(animal.getFamiliar().getEmail()); txtCitaMailFami.setEditable(false);
            txtCitaTipo.setText(animal.getTipo()); txtCitaTipo.setEditable(false);
            
            txtCitaRaza.setText(animal.getRaza()); txtCitaRaza.setEditable(false);
            
            cargarCitasLibres();
            Consultas.cargarVetCombo(cbVetCita);
            
            dialogEditCitas.setLocationRelativeTo(frameClientes);
            dialogEditCitas.setVisible(true);
            dialogEditCitas.setModal(true);
            dialogEditCitas.setSize(800, 650);
        
        }
    }//GEN-LAST:event_btnCitaCliActionPerformed

    private void cbCitaDiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCitaDiaActionPerformed
        // DIALOG_CITAS COMBO HORAS LIBRES
        
        try{
            
            cbCitaHora.removeAllItems();
            for (String hora : horas) 
                cbCitaHora.addItem(hora);
            
            
            Iterator citasit=Consultas.cargarCitas();
            
            while(citasit.hasNext())
            {
                C_Cita cita=(C_Cita)citasit.next();
                
                Date fecha = cita.getFecha();
                Calendar cal = Calendar.getInstance();
                cal.setTime(fecha);
                Calendar day2 = Calendar.getInstance();
                day2.set(Calendar.DATE, Integer.parseInt(cbCitaDia.getSelectedItem().toString()));
                System.out.println("comparo si el dia "+cal.get(Calendar.DAY_OF_MONTH)+" es igual a "+day2.get(Calendar.DAY_OF_MONTH));
                if(cal.get(Calendar.DAY_OF_MONTH)== day2.get(Calendar.DAY_OF_MONTH))
                   
                {
                    //cal.setTime(fecha);
                    
                    //String hora=cal.getTime().getHours()+":"+cal.getTime().getMinutes();
                    String hora=String.valueOf(cal.getTime().getHours());
                    String mins;
                    if(cal.getTime().getMinutes()==0)
                        mins="00";
                    else
                        mins=String.valueOf(cal.getTime().getMinutes());

                    String horaCita = hora+":"+mins;
                    System.out.println("si q es, elimino la hora: "+horaCita);
                    cbCitaHora.removeItem(horaCita);
                }
                
            }
         
        }catch (Exception e) {
            
            System.out.println(e.getMessage());
        }
        
        
        
        
    }//GEN-LAST:event_cbCitaDiaActionPerformed

    private void btnVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnVentaActionPerformed

    private void txtFactTlfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFactTlfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFactTlfActionPerformed

    private void cbFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFiltroActionPerformed
        
        modeloClientes.setRowCount(0);
        String filtro=txtFiltro.getText();
        Iterator animales;
        
        if(txtFiltro.getText().length()==0)
            cargarAnimales();
        else
        {
            switch(cbFiltro.getSelectedIndex()){
                case 0:
                    animales = Consultas.recuperarAnimalesPorNombre(filtro);
                    while(animales.hasNext())
                    {
                        C_Animal animal=(C_Animal)animales.next();
                        Object[] fila= {animal.getId(), animal.getNombre(),animal.getTipo(),animal.getRaza(),animal.getFamiliar().getNombre()};
                        modeloClientes.addRow(fila);

                    }
                    break;
                case 1:
                    if (filtro.length() < 5)
                        filtro=String.format("%05d", Integer.parseInt(filtro));
                    animales = Consultas.recuperarAnimalesPorId(filtro);
                    while(animales.hasNext())
                    {
                        C_Animal animal=(C_Animal)animales.next();
                        Object[] fila= {animal.getId(), animal.getNombre(),animal.getTipo(),animal.getRaza(),animal.getFamiliar().getNombre()};
                        modeloClientes.addRow(fila);

                    }
                    break;
            }
        }
        
        
        
    }//GEN-LAST:event_cbFiltroActionPerformed

    private void txtFiltroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiltroKeyReleased
        
        modeloClientes.setRowCount(0);
        String filtro=txtFiltro.getText();
        Iterator animales;
        
        if(txtFiltro.getText().length()==0)
            cargarAnimales();
        else
        {
            switch(cbFiltro.getSelectedIndex()){
                case 0:
                    animales = Consultas.recuperarAnimalesPorNombre(filtro);
                    while(animales.hasNext())
                    {
                        C_Animal animal=(C_Animal)animales.next();
                        Object[] fila= {animal.getId(), animal.getNombre(),animal.getTipo(),animal.getRaza(),animal.getFamiliar().getNombre()};
                        modeloClientes.addRow(fila);

                    }
                    break;
                case 1:
                    if (filtro.length() < 5)
                        filtro=String.format("%05d", Integer.parseInt(filtro));
                    animales = Consultas.recuperarAnimalesPorId(filtro);
                    while(animales.hasNext())
                    {
                        C_Animal animal=(C_Animal)animales.next();
                        Object[] fila= {animal.getId(), animal.getNombre(),animal.getTipo(),animal.getRaza(),animal.getFamiliar().getNombre()};
                        modeloClientes.addRow(fila);

                    }
                    break;
            }
        }
        
    }//GEN-LAST:event_txtFiltroKeyReleased

    private void cargarComboTipos(int tipo){
        
        cbRazaAni.removeAllItems();
        switch(tipo){
            case 0:
                for (String raza : razasGato) 
                    cbRazaAni.addItem(raza);
                cbVacuna1.setText(vacunasGato[0]); cbVacuna1.setVisible(true);
                cbVacuna2.setText(vacunasGato[1]); cbVacuna2.setVisible(true);
                cbVacuna3.setText(vacunasGato[2]); cbVacuna3.setVisible(true);
                cbVacuna4.setText(vacunasGato[3]); cbVacuna4.setVisible(true);
                cbVacuna5.setText(vacunasGato[4]); cbVacuna5.setVisible(true);
                cbVacuna6.setText(vacunasGato[5]); cbVacuna6.setVisible(true);
                break;
            case 1:
                for (String raza : razasPerro) 
                    cbRazaAni.addItem(raza);
                cbVacuna1.setText(vacunasPerro[0]); cbVacuna1.setVisible(true);
                cbVacuna2.setText(vacunasPerro[1]); cbVacuna2.setVisible(true);
                cbVacuna3.setText(vacunasPerro[2]); cbVacuna3.setVisible(true);
                cbVacuna4.setText(vacunasPerro[3]); cbVacuna4.setVisible(true);
                cbVacuna5.setVisible(false);
                cbVacuna6.setVisible(false);
                break;
            case 2:
                for (String raza : razasPajaro) 
                    cbRazaAni.addItem(raza);
                cbVacuna1.setText(vacunasPajaro[0]); cbVacuna1.setVisible(true);
                cbVacuna2.setText(vacunasPajaro[1]); cbVacuna2.setVisible(true);
                cbVacuna3.setText(vacunasPajaro[2]); cbVacuna3.setVisible(true);
                cbVacuna4.setText(vacunasPajaro[3]); cbVacuna4.setVisible(true);
                cbVacuna5.setVisible(false);
                cbVacuna6.setVisible(false);
                break;
            case 3:
                for (String raza : razasPez) 
                    cbRazaAni.addItem(raza);
                cbVacuna1.setText(vacunasPez[0]); cbVacuna1.setVisible(true);
                cbVacuna2.setText(vacunasPez[1]); cbVacuna2.setVisible(true);
                cbVacuna3.setText(vacunasPez[2]); cbVacuna3.setVisible(true);
                cbVacuna4.setText(vacunasPez[3]); cbVacuna4.setVisible(true);
                cbVacuna5.setVisible(false);
                cbVacuna6.setVisible(false);
                break;
            case 4:
                for (String raza : razasRoedor) 
                    cbRazaAni.addItem(raza);
                cbVacuna1.setText(vacunasRoedor[0]); cbVacuna1.setVisible(true);
                cbVacuna2.setText(vacunasRoedor[1]); cbVacuna2.setVisible(true);
                cbVacuna3.setText(vacunasRoedor[2]); cbVacuna3.setVisible(true);
                cbVacuna4.setText(vacunasRoedor[3]); cbVacuna4.setVisible(true);
                cbVacuna5.setVisible(false);
                cbVacuna6.setVisible(false);
                break;
            case 5:
                for (String raza : razasVaca) 
                    cbRazaAni.addItem(raza);
                cbVacuna1.setText(vacunasVaca[0]); cbVacuna1.setVisible(true);
                cbVacuna2.setText(vacunasVaca[1]); cbVacuna2.setVisible(true);
                cbVacuna3.setText(vacunasVaca[2]); cbVacuna3.setVisible(true);
                cbVacuna4.setText(vacunasVaca[3]); cbVacuna4.setVisible(true);
                cbVacuna5.setVisible(false);
                cbVacuna6.setVisible(false);
                break;
            case 6:
                for (String raza : razasCerdo) 
                    cbRazaAni.addItem(raza);
                cbVacuna1.setText(vacunasCerdo[0]); cbVacuna1.setVisible(true);
                cbVacuna2.setText(vacunasCerdo[1]); cbVacuna2.setVisible(true);
                cbVacuna3.setText(vacunasCerdo[2]); cbVacuna3.setVisible(true);
                cbVacuna4.setText(vacunasCerdo[3]); cbVacuna4.setVisible(true);
                cbVacuna5.setVisible(false);
                cbVacuna6.setVisible(false);
                break;
            case 7:
                for (String raza : razasCaballo) 
                    cbRazaAni.addItem(raza);
                cbVacuna1.setText(vacunasCaballo[0]); cbVacuna1.setVisible(true);
                cbVacuna2.setText(vacunasCaballo[1]); cbVacuna2.setVisible(true);
                cbVacuna3.setText(vacunasCaballo[2]); cbVacuna3.setVisible(true);
                cbVacuna4.setText(vacunasCaballo[3]); cbVacuna4.setVisible(true);
                cbVacuna5.setVisible(false);
                cbVacuna6.setVisible(false);
                break;
            
        }
    }
    private void cbTipoAniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTipoAniActionPerformed
        
        
        Component[] vacunas= panelVacunas.getComponents();
        for (int i=0;i<vacunasGato.length;i++) {
            ((JCheckBox)vacunas[i]).setText(vacunasGato[i]);
        }
       
        cargarComboTipos(cbTipoAni.getSelectedIndex());
        
        
    }//GEN-LAST:event_cbTipoAniActionPerformed

    private void btnVerCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerCliActionPerformed
       
        
        if(tablaClientes.getSelectedRowCount()>0)
        {
            int id=(int)modeloClientes.getValueAt(tablaClientes.getSelectedRow(), 0);
            
            C_Animal animal = Consultas.recuperarAnimalPorId(id);
            
            txID.setText(String.valueOf(animal.getId()));
            txNomb.setText(animal.getNombre());
            txEspecie.setText(animal.getTipo());
            txRaza.setText(animal.getRaza());
            
            if(animal.getSexo()=='M')
                txSexo.setText("Macho");
            else
                txSexo.setText("Hembra");
            
            txFechaNac.setText(String.valueOf(animal.getFecha_nac()));
            txPeso.setText(String.valueOf(animal.getPeso()));
            
            String listaVacunas="";
            Iterator vacunas=animal.getVacunas().iterator();
            
            while(vacunas.hasNext())
            {
                System.out.println("VISTA DE ANIMAL!! sumo vacuna!");
                C_Medicamento vacuna=(C_Medicamento)vacunas.next();
                listaVacunas+="  "+vacuna.getNombre();
            }
            
                        
            txVacunas.setText(listaVacunas);
            txComentario.setText(animal.getComentario());
            
            dialogFichaAnimal.setLocationRelativeTo(frameClientes);
            dialogFichaAnimal.setVisible(true);
            //dialogFichaAnimal.setSize(532,500);
        }
        
        
        
    }//GEN-LAST:event_btnVerCliActionPerformed

    private void btnCitaAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCitaAceptarActionPerformed
        
        String idAnimal=txtCitaId.getText();
        String asunto=txtCitaAsunto.getText();
        String veterinario=cbVetCita.getSelectedItem().toString();
        
        
        Date fecha;
        Date today=new Date();
        Calendar calendar = Calendar.getInstance();
        Time time=Time.valueOf(cbCitaHora.getSelectedItem().toString()+":00");

        
        calendar.setTime(today);
        int mes=cbCitaMes.getSelectedIndex();
        int dia = Integer.parseInt(cbCitaDia.getSelectedItem().toString());
        int hora=time.getHours();
        int mins=time.getMinutes();

        
        calendar.set(Calendar.MONTH, mes);
        calendar.set(Calendar.DAY_OF_MONTH, dia);
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, mins);
        
        fecha=calendar.getTime();
        
        if(update)
        { 
            System.out.println("es update!");
            C_Cita cita= (C_Cita)modeloCitas.getValueAt(tablaCitas.getSelectedRow(), tablaCitas.getSelectedColumn());
            Update.updateCita(veterinario, idAnimal, fecha, asunto, cita);
            update=false;
            
        }
        else
            Guardar.guardarCita(veterinario, idAnimal, fecha, asunto);

        modeloCitas.setRowCount(0);
        cargarCitas();
        
        dialogEditCitas.dispose();
    
    }//GEN-LAST:event_btnCitaAceptarActionPerformed

    private void cbCitaMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCitaMesActionPerformed
        
        
        Calendar calendar = Calendar.getInstance();
        cbCitaDia.removeAllItems();
        
        switch(cbCitaMes.getSelectedIndex()){
            case Calendar.JANUARY:
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                break;
            case Calendar.FEBRUARY:
                calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
                break;
            case Calendar.MARCH:
                calendar.set(Calendar.MONTH, Calendar.MARCH);
                break;
            case Calendar.APRIL:
                calendar.set(Calendar.MONTH, Calendar.APRIL);
                break;
            case Calendar.MAY:
                calendar.set(Calendar.MONTH, Calendar.MAY);
                break;
            case Calendar.JUNE:
                calendar.set(Calendar.MONTH, Calendar.JUNE);
                break;
            case Calendar.JULY:
                calendar.set(Calendar.MONTH, Calendar.JULY);
                break;
            case Calendar.AUGUST:
                calendar.set(Calendar.MONTH, Calendar.AUGUST);
                break;
            case Calendar.SEPTEMBER:
                calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
                break;
            case Calendar.OCTOBER:
                calendar.set(Calendar.MONTH, Calendar.OCTOBER);
                break;
            case Calendar.NOVEMBER:
                calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
                break;
            case Calendar.DECEMBER:
                calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                break;

        }
        
        for(int i=0;i<calendar.getActualMaximum(Calendar.DATE);i++)
                    cbCitaDia.addItem(""+(i+1));
        
        
        
        
    }//GEN-LAST:event_cbCitaMesActionPerformed

    private void cbCitaHoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCitaHoraActionPerformed
        
        try{
            String idAnimal=txtCitaId.getText();
            String asunto=txtCitaAsunto.getText();
            String veterinario=cbVetCita.getSelectedItem().toString();


            Date fecha;
            Date today=new Date();
            Calendar calendar = Calendar.getInstance();
            Time time=Time.valueOf(cbCitaHora.getSelectedItem().toString()+":00");


            calendar.setTime(today);
            int mes=cbCitaMes.getSelectedIndex();
            int dia = Integer.parseInt(cbCitaDia.getSelectedItem().toString());
            int hora=time.getHours();
            int mins=time.getMinutes();



            calendar.set(Calendar.MONTH, mes);
            calendar.set(Calendar.DAY_OF_MONTH, dia);
            calendar.set(Calendar.HOUR_OF_DAY, hora);
            calendar.set(Calendar.MINUTE, mins);

            fecha=calendar.getTime();

            txtResumenCita.setText(fecha.toString());
        }catch(Exception e){}
        
    }//GEN-LAST:event_cbCitaHoraActionPerformed

    private void btnEditarCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarCitaActionPerformed
        
        try{
        if(tablaCitas.getSelectedRowCount()>0 && tablaCitas.getSelectedColumn()>0)
        {
            C_Cita cita= (C_Cita)modeloCitas.getValueAt(tablaCitas.getSelectedRow(), tablaCitas.getSelectedColumn());
            if(cita!=null)
            {
                
                update=true;
                idUpdate=cita.getId();
                clearDialogCitas();
                cbCitaNombreAni.setEnabled(false);
                cbCitaDni.setEnabled(false);
                cbCitaDni.removeAllItems();
                cbCitaDni.addItem(cita.getAnimal().getFamiliar().getDni());
                txtCitaNombreFami.setText(cita.getAnimal().getFamiliar().getNombre());
                txtCitaTipo.setText(cita.getAnimal().getTipo());
                txtCitaTlfFami.setText(cita.getAnimal().getFamiliar().getTelefono());
                txtCitaMailFami.setText(cita.getAnimal().getFamiliar().getEmail());
                cbCitaNombreAni.removeAllItems();
                cbCitaNombreAni.addItem(cita.getAnimal().getNombre());
                txtCitaAsunto.setText(cita.getAsunto());
                txtCitaRaza.setText(cita.getAnimal().getRaza());
                txtCitaId.setText(String.valueOf(cita.getAnimal().getId()));
                
                Date fecha = cita.getFecha();
                Calendar cal=Calendar.getInstance();
                
                cal.setTime(fecha);
                
                cbCitaMes.setSelectedIndex(cal.get(Calendar.MONTH));
                cbCitaDia.setSelectedIndex(cal.get(Calendar.DAY_OF_MONTH)-1);
                
                String hora=String.valueOf(cal.get(Calendar.HOUR));
                String minutos;
                
                if(cal.get(Calendar.MINUTE)==0)
                    minutos="00";
                else
                    minutos=String.valueOf(cal.get(Calendar.MINUTE));
                
                String time=hora+":"+minutos;
                
                cbCitaHora.setSelectedItem(time);

                cbVetCita.removeAllItems();
                cbVetCita.addItem(cita.getVeterinario().getNombre());
                
                dialogEditCitas.setVisible(true);
                dialogEditCitas.setModal(true);
                dialogEditCitas.setSize(800, 650);
                cbCitaNombreAni.setEnabled(false);
                
                
            }
        }
        }catch(Exception e){System.out.println(e.getMessage());}
    }//GEN-LAST:event_btnEditarCitaActionPerformed

    private void btnVetCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVetCancelarActionPerformed
        
        dialogEditVet.dispose();
        update=false;
        clearDialogVet();
    }//GEN-LAST:event_btnVetCancelarActionPerformed

    private void clearDialogVet () {
        
        txtVetNombre.setText("");
        txtVetDni.setText("");
        txtVetEmail.setText("");
        txtVetTlf.setText("");
        txtNumLicencia.setText("");
    }
    private void btnVetAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVetAceptarActionPerformed
        

        String nombre, dni, licencia, tlf, mail;
        
        nombre=txtVetNombre.getText();
        dni=txtVetDni.getText();
        tlf=txtVetTlf.getText();
        mail=txtVetEmail.getText();
        licencia=txtNumLicencia.getText();
        
        C_Veterinario veterinario= new C_Veterinario(dni, nombre, tlf, mail, licencia);
        
        if(update==true)
        {
            Update.updateVet(veterinario,idUpdate);
            update=false;
        }
        else
        {
            Guardar.guardarVet(veterinario);
        }    
        
        dialogEditVet.dispose();
        clearDialogVet();
                
    }//GEN-LAST:event_btnVetAceptarActionPerformed

    private void btnContactoCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContactoCliActionPerformed
        
        
        if(tablaClientes.getSelectedRowCount()>0)
        {
            dialogContacto.setLocationRelativeTo(frameClientes);
            dialogContacto.setVisible(true);
            dialogContacto.setSize(480,225);
         
            int id=Integer.parseInt(modeloClientes.getValueAt(tablaClientes.getSelectedRow(), 0).toString());
            
            C_Animal animal = Consultas.recuperarAnimalPorId(id);
            
            C_Persona familiar=animal.getFamiliar();
            
            txDNI.setText(familiar.getDni());
            txNombre.setText(familiar.getNombre());
            txTel.setText(familiar.getTelefono());
            txEmail.setText(familiar.getEmail());
            txDir.setText(((C_Familiar)familiar).getDireccion());
            
                    
        }
    }//GEN-LAST:event_btnContactoCliActionPerformed

    private void btnCitaCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCitaCancelActionPerformed
        
        update=false;
        dialogEditCitas.dispose();
        //clearDialogCitas();
        
    }//GEN-LAST:event_btnCitaCancelActionPerformed

    private void cbCitaFiltroMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCitaFiltroMesActionPerformed
        
        Calendar cal=Calendar.getInstance();
        cal.setTime(fechaInicio);

        switch(cbCitaFiltroMes.getSelectedIndex()){
            case Calendar.JANUARY:
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                break;
            case Calendar.FEBRUARY:
                cal.set(Calendar.MONTH, Calendar.FEBRUARY);
                break;
            case Calendar.MARCH:
                cal.set(Calendar.MONTH, Calendar.MARCH);
                break;
            case Calendar.APRIL:
                cal.set(Calendar.MONTH, Calendar.APRIL);
                break;
            case Calendar.MAY:
                cal.set(Calendar.MONTH, Calendar.MAY);
                break;
            case Calendar.JUNE:
                cal.set(Calendar.MONTH, Calendar.JUNE);
                break;
            case Calendar.JULY:
                cal.set(Calendar.MONTH, Calendar.JULY);
                break;
            case Calendar.AUGUST:
                cal.set(Calendar.MONTH, Calendar.AUGUST);
                break;
            case Calendar.SEPTEMBER:
                cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
                break;
            case Calendar.OCTOBER:
                cal.set(Calendar.MONTH, Calendar.OCTOBER);
                break;
            case Calendar.NOVEMBER:
                cal.set(Calendar.MONTH, Calendar.NOVEMBER);
                break;
            case Calendar.DECEMBER:
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                break;
        }
        
        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
        
        while(cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
            cal.add(Calendar.DATE,1);
        }
        
        
        fechaInicio=cal.getTime();
        cal.add(Calendar.DATE, 7);
        fechaFin=cal.getTime();
        //modeloCitas.setRowCount(0);
        cargarCitas();
        cbCitaFiltroSemana.setSelectedIndex(0);


    }//GEN-LAST:event_cbCitaFiltroMesActionPerformed

    private void cbCitaFiltroSemanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCitaFiltroSemanaActionPerformed
        
        Calendar cal=Calendar.getInstance();
        cal.setTime(fechaInicio);
        cal.set(Calendar.HOUR_OF_DAY, 6);
        
        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
        while(cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
            cal.add(Calendar.DATE,1);
        }
        
        switch(cbCitaFiltroSemana.getSelectedIndex()){
            case 0:
                fechaInicio=cal.getTime();
                cal.add(Calendar.DATE, 7);
                fechaFin=cal.getTime();
                break;
            case 1:
                cal.add(Calendar.DATE, 7);
                fechaInicio=cal.getTime();
                cal.add(Calendar.DATE, 7);
                fechaFin=cal.getTime();
                break;
            case 2:
                cal.add(Calendar.DATE, 14);
                fechaInicio=cal.getTime();
                cal.add(Calendar.DATE, 7);
                fechaFin=cal.getTime();
                break;
            case 3:
                cal.add(Calendar.DATE, 21);
                fechaInicio=cal.getTime();
                cal.add(Calendar.DATE, 7);
                fechaFin=cal.getTime();
                break;
                
        }
        
        System.out.println("las fechas finales son"+fechaInicio.toString()+" yy  "+fechaFin.toString());
        
        modeloCitas.setRowCount(0);
        cargarCitas();
        
        
    }//GEN-LAST:event_cbCitaFiltroSemanaActionPerformed

    private void cbCitaNombreAniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCitaNombreAniActionPerformed
        

        try{
            String dni= cbCitaDni.getSelectedItem().toString();
            C_Persona familiar= Consultas.recuperarUnaPersonaPordni(dni);

            if(familiar != null){
                Iterator animales=Consultas.recuperarAnimalesPorIdFamiliar(familiar.getId());
                while(animales.hasNext())
                {
                    C_Animal animal=(C_Animal)animales.next();
                    if(animal.getNombre().compareTo(cbCitaNombreAni.getSelectedItem().toString())==0)
                    {
                        txtCitaId.setText(String.valueOf(animal.getId()));
                        txtCitaTipo.setText(animal.getTipo());
                        txtCitaRaza.setText(animal.getRaza());  
                    }
                }

            }
        }catch(Exception e){}
        
        
    }//GEN-LAST:event_cbCitaNombreAniActionPerformed

    private void cbVetDiagnosticosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbVetDiagnosticosActionPerformed
        
        try{
            C_Veterinario vet=Consultas.recuperarVeterinarioPorNombre(cbVetDiagnosticos.getSelectedItem().toString());

            Iterator itcitas=Consultas.cargarCitasporVet(vet.getId());

            modeloListaCitas=new DefaultListModel();


            while(itcitas.hasNext())
            {
                C_Cita cita=(C_Cita)itcitas.next();
                modeloListaCitas.addElement(cita);
            }


            listaCitasVet.setModel(modeloListaCitas);
        
        }catch(Exception e){}
        
    }//GEN-LAST:event_cbVetDiagnosticosActionPerformed

    private void clearDatosDiagnostico () {
        
        txNombreAniDiag.setText("");
        txRazaDiag.setText("");
        txFechaNacDiag.setText("");
        txTipoDiag.setText("");
        txSexoDiag.setText("");
        txhoraCitaDiag.setText("");
        txAsuntoDiag.setText("");
        //txAsuntoDiag.setVisible(false);
        txDiagnostico.setEnabled(false);
        txTratamiento.setEnabled(false);
        txDiagnostico.setText("");
        txTratamiento.setText("");
        listaMedicamentos.setEnabled(false);
        listaMedicamentosBD.setEnabled(false);
        txtFiltroMed.setText("");
        txtFiltroMed.setEnabled(false);
        btnMedicamento.setEnabled(false);
        btnFinDiag.setEnabled(false);
        chkMedicacion.setEnabled(false);
        chkMedicacion.setSelected(false);
        btnEliminarMed.setEnabled(false);
        
        
        modeloListaMedicamentos=new DefaultListModel();
        listaMedicamentos.setModel(modeloListaMedicamentos);
        
        //llenar la lista con TODOS los medicamentos de la BD
        Iterator medicamentos=Consultas.cargarMedicamentos();
        modeloListaMedicamentosBD=new DefaultListModel();
        while(medicamentos.hasNext())
        {
            C_Medicamento medicamento = (C_Medicamento)medicamentos.next();
            modeloListaMedicamentosBD.addElement(medicamento);
        }
        listaMedicamentosBD.setModel(modeloListaMedicamentosBD);
        
        
        
        
    }
    private void listaCitasVetValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listaCitasVetValueChanged
        
        clearDatosDiagnostico();
        if(listaCitasVet.getSelectedIndex()!= -1 )
        {
            txDiagnostico.setEnabled(true);
            txTratamiento.setEnabled(true);
            C_Cita cita=(C_Cita)modeloListaCitas.getElementAt(listaCitasVet.getSelectedIndex());
            
            txNombreAniDiag.setText(cita.getAnimal().getNombre());
            txRazaDiag.setText(cita.getAnimal().getRaza());
            txFechaNacDiag.setText(cita.getAnimal().getFecha_nac().toString());
            txTipoDiag.setText(cita.getAnimal().getTipo());
            txSexoDiag.setText(String.valueOf(cita.getAnimal().getSexo()));
            txhoraCitaDiag.setText(cita.getFecha().toString());
            txAsuntoDiag.setText(cita.getAsunto());
            btnFinDiag.setEnabled(true);
            chkMedicacion.setEnabled(true);
            //txAsuntoDiag.setVisible(true);
            
        }
        
        
        
        
    }//GEN-LAST:event_listaCitasVetValueChanged

    private void btnDiagnosticosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosticosActionPerformed

        cambiarPanel(panelDiagnosticos);
        Consultas.cargarVetCombo(cbVetDiagnosticos);

        C_Veterinario vet=Consultas.recuperarVeterinarioPorNombre(cbVetDiagnosticos.getSelectedItem().toString());

        Iterator itcitas=Consultas.cargarCitasporVet(vet.getId());

        modeloListaCitas=new DefaultListModel();

        while(itcitas.hasNext())
        {
            C_Cita cita=(C_Cita)itcitas.next();
            modeloListaCitas.addElement(cita);
        }
        listaCitasVet.setModel(modeloListaCitas);
        
        clearDatosDiagnostico();
        
        

    }//GEN-LAST:event_btnDiagnosticosActionPerformed

    private void btnFinDiagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinDiagActionPerformed
        
        C_Cita cita=(C_Cita)modeloListaCitas.getElementAt(listaCitasVet.getSelectedIndex());
        C_Veterinario vet = cita.getVeterinario();
        
        String tratamiento = txTratamiento.getText();
        String descripcion = txDiagnostico.getText();
        
        C_Diagnostico diagnostico = new C_Diagnostico (tratamiento, descripcion, vet, cita.getAnimal());
        for(int i=0;i<modeloListaMedicamentos.getSize();i++)
        {
            C_Medicamento med=(C_Medicamento)modeloListaMedicamentos.getElementAt(i);
            diagnostico.getMedicamentos().add(med);
        }
        
        Guardar.guardarDiagnostico(diagnostico);
        Eliminar.EliminarCita(cita);
        clearDatosDiagnostico();
        modeloListaCitas.removeElement(cita);
        
        
        ////¿Que hago ahora con el diagnostico?
        //lo cargamos en el comboUltimosDiagnosticos
        
        if(comboBox5Ultimos.getItemCount()<5)
            comboBox5Ultimos.addItem(String.valueOf(diagnostico.getId()));
        else
        {
            comboBox5Ultimos.removeItemAt(0);
            comboBox5Ultimos.addItem(String.valueOf(diagnostico.getId()));
        }
            
        
        
    }//GEN-LAST:event_btnFinDiagActionPerformed

    private void chkMedicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMedicacionActionPerformed
        
        if(chkMedicacion.isSelected())
        {
            listaMedicamentos.setEnabled(true);
            listaMedicamentosBD.setEnabled(true);
            txtFiltroMed.setEnabled(true);
            btnMedicamento.setEnabled(true);
            btnEliminarMed.setEnabled(true);
            
                    
        }
        else
        {
            listaMedicamentos.setEnabled(false);
            listaMedicamentosBD.setEnabled(false);
            txtFiltroMed.setEnabled(false);
            btnMedicamento.setEnabled(false);
            btnEliminarMed.setEnabled(false);
        }
    }//GEN-LAST:event_chkMedicacionActionPerformed

    private void btnMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMedicamentoActionPerformed
        
        if(listaMedicamentosBD.getSelectedIndex() != -1)
        {
            C_Medicamento medicamento = (C_Medicamento) modeloListaMedicamentosBD.getElementAt(listaMedicamentosBD.getSelectedIndex());
            modeloListaMedicamentos.addElement(medicamento);
        }
    }//GEN-LAST:event_btnMedicamentoActionPerformed

    private void btnEliminarMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarMedActionPerformed
        
        if(listaMedicamentos.getSelectedIndex() != -1)
        {
            modeloListaMedicamentos.remove(listaMedicamentos.getSelectedIndex());
        }
        
    }//GEN-LAST:event_btnEliminarMedActionPerformed

    private void txtFiltroMedKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiltroMedKeyReleased
        
        modeloListaMedicamentosBD=new DefaultListModel();
        
        String filtro=txtFiltroMed.getText();
        Iterator medicamentos;
        
        if(txtFiltroMed.getText().length()!=0)
        {
            medicamentos = Consultas.recuperarMedicamentosPorNombre(filtro);
            while(medicamentos.hasNext())
            {
                C_Medicamento medicamento=(C_Medicamento)medicamentos.next();
                modeloListaMedicamentosBD.addElement(medicamento);
            }
        }
        else{
            //cargar todos
            medicamentos=Consultas.cargarMedicamentos();
            while(medicamentos.hasNext())
            {
                C_Medicamento medicamento = (C_Medicamento)medicamentos.next();
                modeloListaMedicamentosBD.addElement(medicamento);
            }
            listaMedicamentosBD.setModel(modeloListaMedicamentosBD);
        }
        listaMedicamentosBD.setModel(modeloListaMedicamentosBD);
        
    }//GEN-LAST:event_txtFiltroMedKeyReleased

    private void comboBox5UltimosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBox5UltimosActionPerformed
        
        
        C_Diagnostico diagnostico=Consultas.recuperarDiagnosticoPorId(Integer.parseInt(comboBox5Ultimos.getSelectedItem().toString()));
        
        txaNombre.setText(diagnostico.getAnimal().getNombre());
        txaContacto.setText(diagnostico.getAnimal().getFamiliar().getTelefono());
        txaTratamiento.setText(diagnostico.getTratamiento());
        txaDiagnostico.setText(diagnostico.getDescripcion());
        
        Iterator medicamentos=diagnostico.getMedicamentos().iterator();
        String salida="";
        while(medicamentos.hasNext())
        {
            C_Medicamento m=(C_Medicamento)medicamentos.next();
            salida+="   "+m.getNombre();
        }
        txaMedicacion.setText(salida);
        
    }//GEN-LAST:event_comboBox5UltimosActionPerformed

    private void btnVerCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerCitaActionPerformed
        
        if(tablaCitas.getSelectedRowCount()>0 && tablaCitas.getSelectedColumn()>0)
        {
            C_Cita cita= (C_Cita)modeloCitas.getValueAt(tablaCitas.getSelectedRow(), tablaCitas.getSelectedColumn());
            if(cita!=null)
            {
                dialogDetallesCita.setVisible(true);
                dialogDetallesCita.setSize(350, 250);
                dialogDetallesCita.setLocationRelativeTo(frameClientes);
                txVet.setText(cita.getVeterinario().getNombre());
                txCli.setText(cita.getAnimal().getNombre());
                txTelFam.setText(cita.getAnimal().getFamiliar().getTelefono());
                txFam.setText(cita.getAnimal().getFamiliar().getNombre());
                txHora.setText(cita.getFecha().toString());
                txAsunto.setText(cita.getAsunto());        
            }
        }
        
    }//GEN-LAST:event_btnVerCitaActionPerformed

    private void btnEliminarCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarCitaActionPerformed
        
        
        if(tablaCitas.getSelectedRowCount()>0 && tablaCitas.getSelectedColumn()>0)
        {
            C_Cita cita= (C_Cita)modeloCitas.getValueAt(tablaCitas.getSelectedRow(), tablaCitas.getSelectedColumn());
            if(cita!=null)
            {   
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog (frameClientes, "¿Esta Seguro que desea eliminar este registro?","Warning",dialogButton);
            
                if(dialogResult == JOptionPane.YES_OPTION){
            
                    Eliminar.EliminarCita(cita);
                    cargarCitas();
                }
            }
        }
    }//GEN-LAST:event_btnEliminarCitaActionPerformed

    private void btnConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfigActionPerformed
        
        dialogConfiguracion.setVisible(true);
        dialogConfiguracion.setSize(435, 320);
    }//GEN-LAST:event_btnConfigActionPerformed

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        
        modo = cbGestor.getSelectedIndex();
        
        timer = new Timer(50, new ActionListener() {
            int counter = 10;
            public void actionPerformed(ActionEvent ae) {
                counter++;
                pbLoading.setValue(counter);
                if (pbLoading.getValue()==100) {
                    timer.stop();
                    MainWindow.this.setVisible(false);
                    frameClientes.setSize(950, 750);
                    frameClientes.setLocationRelativeTo(null);
                    frameClientes.setVisible(true);
                } 
            }
        });
        if(modo ==0)
        {
            CrearTablas.crearTablas();
            HibernateUtil.buildSessionFactory();
        }
        
        
        if(demo)
        {
            try{
                DatosDemo.cargarTodos();
            }catch(Exception e){System.out.println(e.getMessage());System.out.println("MAL DATOS!!");}
        }
        
        cargarAnimales();
        cargarCitas();
        
        timer.start();
        
        
        Calendar cal = Calendar.getInstance();
        fechaInicio=new Date();
        cal.setTime(fechaInicio);
        cal.add(Calendar.DATE, 7);
        fechaFin=cal.getTime();
        
        
        cal.setTime(fechaInicio);
        
        cbCitaFiltroMes.setSelectedIndex(cal.get(Calendar.MONTH));
        
        
    }//GEN-LAST:event_btnPlayActionPerformed

    private void cbGestorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGestorActionPerformed
        switch (cbGestor.getSelectedIndex()) {
            case 0:
                lbIconoGestor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/hibernate.png")));
                break;
            case 1:
                lbIconoGestor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/neodatis.png")));
                break;
            case 3:
                lbIconoGestor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/x.png")));
                break;
            default:
                break;
        }
    }//GEN-LAST:event_cbGestorActionPerformed

    private void btAceptarConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAceptarConfigActionPerformed
        dialogConfiguracion.dispose();
        if(checkDemo.isSelected())
            demo=true;
        else
            demo=false;
        
        
    }//GEN-LAST:event_btAceptarConfigActionPerformed

    private void btCancelarConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarConfigActionPerformed
        dialogConfiguracion.dispose();
        cbGestor.setSelectedIndex(modo);
    }//GEN-LAST:event_btCancelarConfigActionPerformed

    private void btnMailCli1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMailCli1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMailCli1ActionPerformed

    private void btnNuevoVetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoVetActionPerformed

        clearDialogVet();
        dialogEditVet.setLocationRelativeTo(frameClientes);
        dialogEditVet.setVisible(true);

        dialogEditVet.setSize(410, 330);

    }//GEN-LAST:event_btnNuevoVetActionPerformed

    private void btnEditarVetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarVetActionPerformed

        dialogEditVet.setLocationRelativeTo(frameClientes);
        dialogEditVet.setVisible(true);

        dialogEditVet.setSize(410, 330);
        if(tablaVeterinarios.getSelectedRowCount()>0)
        {
            int id=Integer.parseInt(modeloVet.getValueAt(tablaVeterinarios.getSelectedRow(), 0).toString());

            C_Veterinario vet = Consultas.recuperarVeterinarioPorId(id);
            txtVetNombre.setText(vet.getNombre());
            txtVetDni.setText(vet.getDni());
            txtVetTlf.setText(vet.getTelefono());
            txtVetEmail.setText(vet.getEmail());
            txtNumLicencia.setText(vet.getNumLicencia());
            update=true;
            idUpdate=id;
        }

    }//GEN-LAST:event_btnEditarVetActionPerformed

    private void btnEliminarVetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarVetActionPerformed

        if(tablaVeterinarios.getSelectedRowCount()>0)
        {
            int id=(int)modeloVet.getValueAt(tablaVeterinarios.getSelectedRow(), 0);
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (frameClientes, "¿Esta Seguro que desea eliminar este registro?","Warning",dialogButton);

            if(dialogResult == JOptionPane.YES_OPTION){

                Eliminar.EliminarVeterinario(id);

                modeloVet.removeRow(tablaVeterinarios.getSelectedRow());
            }
        }

    }//GEN-LAST:event_btnEliminarVetActionPerformed

    private void btnInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInfoActionPerformed
        dialogInfo.setVisible(true);
        dialogInfo.setSize(365,260);
        dialogInfo.setLocationRelativeTo(frameClientes);
    }//GEN-LAST:event_btnInfoActionPerformed

    private void dialogPremiumWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dialogPremiumWindowClosing
        cambiarPanel(panelClientes);
        
    }//GEN-LAST:event_dialogPremiumWindowClosing

    private void btnVerCli1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerCli1ActionPerformed
        
        dialogUltimosDiagnosticos.setLocationRelativeTo(frameClientes);
        dialogUltimosDiagnosticos.setVisible(true);
        dialogUltimosDiagnosticos.setSize(500, 550);
    }//GEN-LAST:event_btnVerCli1ActionPerformed


    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            public void run() {
                MainWindow main= new MainWindow();
                main.setLocationRelativeTo(null);
                main.setVisible(true);
                
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelDatosMed;
    private javax.swing.JButton btAceptarConfig;
    private javax.swing.JButton btCancelarConfig;
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCitaAceptar;
    private javax.swing.JButton btnCitaCancel;
    private javax.swing.JButton btnCitaCli;
    private javax.swing.JButton btnCitaCli1;
    private javax.swing.JButton btnCitas;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnComprar;
    private javax.swing.JButton btnConfig;
    private javax.swing.JButton btnContactoCli;
    private javax.swing.JButton btnContactoCli1;
    private javax.swing.JButton btnDiagnosticos;
    private javax.swing.JButton btnEditarCita;
    private javax.swing.JButton btnEditarCli;
    private javax.swing.JButton btnEditarCli3;
    private javax.swing.JButton btnEditarVet;
    private javax.swing.JButton btnEliminarCita;
    private javax.swing.JButton btnEliminarCli;
    private javax.swing.JButton btnEliminarCli3;
    private javax.swing.JButton btnEliminarMed;
    private javax.swing.JButton btnEliminarVet;
    private javax.swing.JButton btnFacturas;
    private javax.swing.JButton btnFinDiag;
    private javax.swing.JButton btnInfo;
    private javax.swing.JButton btnInventario;
    private javax.swing.JButton btnMailCli;
    private javax.swing.JButton btnMailCli1;
    private javax.swing.JButton btnMedicamento;
    private javax.swing.JButton btnModificarFact;
    private javax.swing.JButton btnNuevaCita;
    private javax.swing.JButton btnNuevoCli;
    private javax.swing.JButton btnNuevoVet;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnVenta;
    private javax.swing.JButton btnVerCita;
    private javax.swing.JButton btnVerCli;
    private javax.swing.JButton btnVerCli1;
    private javax.swing.JButton btnVetAceptar;
    private javax.swing.JButton btnVetCancelar;
    private javax.swing.JButton btnVeterinarios;
    private static javax.swing.JComboBox<String> cbCitaDia;
    private static javax.swing.JComboBox<String> cbCitaDni;
    private javax.swing.JComboBox<String> cbCitaFiltroMes;
    private javax.swing.JComboBox<String> cbCitaFiltroSemana;
    private static javax.swing.JComboBox<String> cbCitaHora;
    private static javax.swing.JComboBox<String> cbCitaMes;
    private javax.swing.JComboBox<String> cbCitaNombreAni;
    private static javax.swing.JComboBox<String> cbDniFami;
    private javax.swing.JComboBox<String> cbFiltro;
    private javax.swing.JComboBox<String> cbGestor;
    private javax.swing.JComboBox<String> cbRazaAni;
    private javax.swing.JComboBox<String> cbTipoAni;
    private javax.swing.JCheckBox cbVacuna1;
    private javax.swing.JCheckBox cbVacuna2;
    private javax.swing.JCheckBox cbVacuna3;
    private javax.swing.JCheckBox cbVacuna4;
    private javax.swing.JCheckBox cbVacuna5;
    private javax.swing.JCheckBox cbVacuna6;
    private javax.swing.JComboBox<String> cbVetCita;
    private javax.swing.JComboBox<String> cbVetDiagnosticos;
    private javax.swing.JCheckBox checkDemo;
    private javax.swing.JCheckBox chkConsulta;
    private javax.swing.JCheckBox chkMedicacion;
    private javax.swing.JComboBox<String> comboBox5Ultimos;
    private javax.swing.JDialog dialogConfiguracion;
    private javax.swing.JDialog dialogContacto;
    private javax.swing.JDialog dialogDetallesCita;
    private javax.swing.JDialog dialogEditCitas;
    public javax.swing.JDialog dialogEditClientes;
    private javax.swing.JDialog dialogEditVet;
    private javax.swing.JDialog dialogFichaAnimal;
    private javax.swing.JDialog dialogInfo;
    private javax.swing.JDialog dialogPremium;
    private javax.swing.JDialog dialogUltimosDiagnosticos;
    private javax.swing.JFrame frameClientes;
    private javax.swing.JLabel icContacto;
    private javax.swing.JLabel imgProducto;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;

    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;

    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lb16_17;
    private javax.swing.JLabel lbAAD;
    private javax.swing.JLabel lbAsunto;
    private javax.swing.JLabel lbAño;
    private javax.swing.JLabel lbCiclo;
    private javax.swing.JLabel lbCitaAsunto;
    private javax.swing.JLabel lbCitaId;
    private javax.swing.JLabel lbCitaNom;
    private javax.swing.JLabel lbCitaRaza;
    private javax.swing.JLabel lbCitaTipo;
    private javax.swing.JLabel lbCitasVeterinario;
    private javax.swing.JLabel lbCli;
    private javax.swing.JLabel lbComentarioCli;
    private javax.swing.JLabel lbComentarios;
    private javax.swing.JLabel lbContacto;
    private javax.swing.JLabel lbContraseña;
    private javax.swing.JLabel lbCreado;
    private javax.swing.JLabel lbDAM;
    private javax.swing.JLabel lbDNI;
    private javax.swing.JLabel lbDavid;
    private javax.swing.JLabel lbDiagAnimal;
    private javax.swing.JLabel lbDiagDiagnostico;
    private javax.swing.JLabel lbDir;
    private javax.swing.JLabel lbDireFami;
    private javax.swing.JLabel lbDniCli;
    private javax.swing.JLabel lbDniCli2;
    private javax.swing.JLabel lbEmail;
    private javax.swing.JLabel lbEspecie;
    private javax.swing.JLabel lbFam;
    private javax.swing.JLabel lbFechaFactura;
    private javax.swing.JLabel lbFechaNac;
    private javax.swing.JLabel lbFecha_nacAni;
    private javax.swing.JLabel lbFechasCita;
    private javax.swing.JLabel lbFlt;
    private javax.swing.JLabel lbFotoPerfil;
    private javax.swing.JLabel lbGestor;
    private javax.swing.JLabel lbHora;
    private javax.swing.JLabel lbID;
    private javax.swing.JLabel lbIDInv;
    private javax.swing.JLabel lbIconoGestor;
    private javax.swing.JLabel lbLogo;
    private javax.swing.JLabel lbMailFami;
    private javax.swing.JLabel lbMailFami5;
    private javax.swing.JLabel lbMedicacion;
    private javax.swing.JLabel lbMiguel;
    private javax.swing.JLabel lbModulo;
    private javax.swing.JLabel lbNCitas;
    private javax.swing.JLabel lbNomb;
    private javax.swing.JLabel lbNombre;
    private javax.swing.JLabel lbNombreAni;
    private javax.swing.JLabel lbNombreFami;
    private javax.swing.JLabel lbNombreFami2;
    private javax.swing.JLabel lbNombreVet;
    private javax.swing.JLabel lbNumFactura;
    private javax.swing.JLabel lbNum_Licencia;
    private javax.swing.JLabel lbPeso;
    private javax.swing.JLabel lbPesoCli;
    private javax.swing.JLabel lbPrecioInv;
    private javax.swing.JLabel lbRaza;
    private javax.swing.JLabel lbRazaAni;
    private javax.swing.JLabel lbResumenCita;
    private javax.swing.JLabel lbSaul;
    private javax.swing.JLabel lbSemana;
    private javax.swing.JLabel lbSexo;
    private javax.swing.JLabel lbTel;
    private javax.swing.JLabel lbTelFam;
    private javax.swing.JLabel lbTipTratamiento;
    private javax.swing.JLabel lbTipoAni;
    private javax.swing.JLabel lbTlfFami;
    private javax.swing.JLabel lbTlfFami5;
    private javax.swing.JLabel lbURL;
    private javax.swing.JLabel lbUsuario;
    private javax.swing.JLabel lbVacunas;
    private javax.swing.JLabel lbVet;
    private javax.swing.JLabel lbVetCita;
    private javax.swing.JLabel lbVetDni;
    private javax.swing.JLabel lbVetEmail;
    private javax.swing.JLabel lbVetNombre;
    private javax.swing.JLabel lbVetTlf;
    private javax.swing.JLabel lbmm;
    private javax.swing.JLabel lbtrat;
    private javax.swing.JList<String> listaCitasVet;
    private javax.swing.JList<String> listaMedicamentos;
    private javax.swing.JList<String> listaMedicamentosBD;
    private javax.swing.JLabel mypets;
    private javax.swing.JRadioButton newRbHembra;
    private javax.swing.JRadioButton newRbMacho;
    private javax.swing.JPanel panelCitas;
    private javax.swing.JScrollPane panelCitasList;
    private javax.swing.JPanel panelClientes;
    private javax.swing.JPanel panelConfigDB;
    private javax.swing.JPanel panelContactoClientes;
    private javax.swing.JPanel panelContactoClientes1;
    private javax.swing.JPanel panelContactoClientes2;
    private javax.swing.JPanel panelDatosAniCita;
    private javax.swing.JPanel panelDatosAnimal;
    private javax.swing.JPanel panelDatosCita;
    private javax.swing.JPanel panelDatosClientes;
    private javax.swing.JPanel panelDatosClientes2;
    private javax.swing.JPanel panelDatosClientes3;
    private javax.swing.JPanel panelDatosClinicos;
    private javax.swing.JPanel panelDatosDiag;
    private javax.swing.JPanel panelDatosFacturas;
    private javax.swing.JPanel panelDatosFamiliar;
    private javax.swing.JPanel panelDatosFamiliar5;
    private javax.swing.JPanel panelDatosFichaCli;
    private javax.swing.JPanel panelDatosVet;
    private javax.swing.JPanel panelDetalles;
    private javax.swing.JPanel panelDiagnosticos;
    private javax.swing.JPanel panelFacturas;
    private javax.swing.JPanel panelFiltrosClientes;
    private javax.swing.JPanel panelInfo;
    private javax.swing.JPanel panelInventario;
    private javax.swing.JPanel panelTratamientos;
    private javax.swing.JPanel panelVacunas;
    private javax.swing.JPanel panelVerClientes;
    private javax.swing.JPanel panelVerClientes1;
    private javax.swing.JPanel panelVerClientes2;
    private javax.swing.JPanel panelVeterinarios;
    private javax.swing.JProgressBar pbLoading;
    private javax.swing.ButtonGroup rbgSexo;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JScrollPane scrollTabla1;
    private static javax.swing.JTable tablaCitas;
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTable tablaVeterinarios;
    private javax.swing.JLabel tbdesc;
    private javax.swing.JLabel txAsunto;
    private javax.swing.JLabel txAsuntoDiag;
    private javax.swing.JLabel txCli;
    private javax.swing.JTextArea txComentario;
    private javax.swing.JTextField txContraseña;
    private javax.swing.JLabel txDNI;
    private javax.swing.JTextArea txDiagnostico;
    private javax.swing.JLabel txDir;
    private javax.swing.JLabel txEmail;
    private javax.swing.JLabel txEspecie;
    private javax.swing.JLabel txFam;
    private javax.swing.JLabel txFechaNac;
    private javax.swing.JLabel txFechaNacDiag;
    private javax.swing.JLabel txHora;
    private javax.swing.JLabel txID;
    private javax.swing.JLabel txNomb;
    private javax.swing.JLabel txNombre;
    private javax.swing.JLabel txNombreAniDiag;
    private static javax.swing.JLabel txNumCitas;
    private javax.swing.JLabel txPeso;
    private javax.swing.JLabel txRaza;
    private javax.swing.JLabel txRazaDiag;
    private static javax.swing.JLabel txSemana;
    private javax.swing.JLabel txSexo;
    private javax.swing.JLabel txSexoDiag;
    private javax.swing.JLabel txTel;
    private javax.swing.JLabel txTelFam;
    private javax.swing.JLabel txTipoDiag;
    private javax.swing.JTextArea txTratamiento;
    private javax.swing.JTextField txURL;
    private javax.swing.JTextField txUsuario;
    private javax.swing.JLabel txVacunas;
    private javax.swing.JLabel txVet;
    private javax.swing.JLabel txaContacto;
    private javax.swing.JTextArea txaDiagnostico;
    private javax.swing.JLabel txaMedicacion;
    private javax.swing.JLabel txaNombre;
    private javax.swing.JTextArea txaTratamiento;
    private javax.swing.JLabel txhoraCitaDiag;
    private static javax.swing.JLabel txmesc;
    private javax.swing.JTextField txtChipidCli;
    private javax.swing.JTextArea txtCitaAsunto;
    private javax.swing.JTextField txtCitaId;
    private javax.swing.JTextField txtCitaMailFami;
    private javax.swing.JTextField txtCitaNombreFami;
    private javax.swing.JTextField txtCitaRaza;
    private javax.swing.JTextField txtCitaTipo;
    private javax.swing.JTextField txtCitaTlfFami;
    private javax.swing.JTextArea txtComentarioCli;
    private javax.swing.JTextField txtDireFami;
    private javax.swing.JTextField txtDniFact;
    private javax.swing.JTextField txtFactTlf;
    private javax.swing.JTextField txtFechaCli;
    private javax.swing.JTextField txtFechaFactura;
    private static javax.swing.JTextField txtFiltro;
    private javax.swing.JTextField txtFiltroMed;
    private javax.swing.JTextField txtIDInv;
    private javax.swing.JTextField txtMailFact;
    private javax.swing.JTextField txtMailFami;
    private javax.swing.JTextField txtNombreCli;
    private javax.swing.JTextField txtNombreFact;
    private javax.swing.JTextField txtNombreFami;
    private javax.swing.JTextField txtNombreVet;
    private javax.swing.JTextField txtNumFactura;
    private javax.swing.JTextField txtNumLicencia;
    private javax.swing.JTextField txtPesoCli;
    private javax.swing.JTextField txtPrecioInv;
    private javax.swing.JTextArea txtResumenCita;
    private javax.swing.JTextField txtTlfFami;
    private javax.swing.JTextField txtVetDni;
    private javax.swing.JTextField txtVetEmail;
    private javax.swing.JTextField txtVetNombre;
    private javax.swing.JTextField txtVetTlf;
    // End of variables declaration//GEN-END:variables
}

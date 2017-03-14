package hibernate_clinicaveterinaria;
import POJOS.*;
import funciones.*;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Miguel
 */
public class MainWindow extends javax.swing.JFrame {

    Timer timer;
    static DefaultTableModel modeloClientes;
    static Object[][] citas = new Object[38][8];
    static String[] horas = {"9:00","9:15","9:30","9:45","10:00","10:15","10:30","10:45",
                              "11:00","11:15","11:30","11:45","12:00","12:15","12:30","12:45",
                              "13:00","13:15","13:30","13:45","     ",
                              "17:00","17:15","17:30","17:45","18:00","18:15","18:30","18:45",
                              "19:00","19:15","19:30","19:45","20:00","20:15","20:30","20:45"};
    
    public MainWindow() {
        
        initComponents();
//
        timer = new Timer(50, new ActionListener() {
            int counter = 10;
            public void actionPerformed(ActionEvent ae) {
                counter++;
                pbLoading.setValue(counter);
                if (pbLoading.getValue()==100) {
                    timer.stop();
                    MainWindow.this.setVisible(false);
                    frameClientes.setSize(915, 750);
                    frameClientes.setVisible(true);
                } 
            }
        });
        
        panelClientes.setVisible(true);
        panelCitas.setVisible(false);
        panelVeterinarios.setVisible(false);
        panelFacturas.setVisible(false);
        panelInventario.setVisible(false);
        
        timer.start();
        CrearTablas.crearTablas();
        
        //////
        
        String[] columnasClientes={"id", "Nombre", "Tipo", "Raza", "Familiar"};
        modeloClientes=new DefaultTableModel(columnasClientes,0);
        tablaClientes.setModel(modeloClientes);
        
        cargarAnimales();
        
        
        JTextComponent editor = (JTextComponent) cbDniFami.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                 if(cbDniFami.getEditor().getItem().toString().length()>0)
                 {
                     System.out.println("leng mayor q 0 -->"+cbDniFami.getEditor().getItem().toString()+".");
                     String dni=cbDniFami.getEditor().getItem().toString();
                     System.out.println("voy a buscar dni:"+dni);

                     cbDniFami.removeAllItems();
                     cbDniFami.addItem(dni);

                     Session sesion=HibernateUtil.getSession();
                     Query qry = sesion.createQuery("FROM POJOS.C_Persona f WHERE f.dni like ?");
                     qry.setString(0, dni+"%");
                     Iterator familiares =qry.list().iterator();



                     while(familiares.hasNext())
                     {
                         System.out.println("tengo un familiar con el dni parecido");
                         C_Persona familiar=(C_Familiar)familiares.next();

                         cbDniFami.addItem(familiar.getDni());
                     }
                     sesion.close();
                 }
                 else{
                     System.out.println("leng corta");
                     cargarFamiliares();
                 }
                
            }
         });
        
        ////prueba
        
        
        
        ///fin prueba
       
        
    }
    public static void cargarFamiliares() {
        
        cbDniFami.removeAllItems();
        cbDniFami.addItem("");
        
        try{
            Session sesion=HibernateUtil.getSession();
            Iterator familiares = sesion.createCriteria(C_Familiar.class).list().iterator();
            
            while(familiares.hasNext())
            {
                System.out.println("tengo un familiar");
                C_Persona familiar=(C_Familiar)familiares.next();
                
                cbDniFami.addItem(familiar.getDni());
                
                
                
            }
            sesion.close();
         
        }catch (Exception e) {
            
            System.out.println(e.getMessage());
        }  
    }
    
    public static void cargarCitasLibres(){
        
        //Date today=new Date();
        Calendar cal=Calendar.getInstance();
        
        cbCitaDia.removeAllItems();
        cbCitaHora.removeAllItems();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

        Date ultimodia=cal.getTime();
        Date today=new Date();
        
        System.out.println("hoy->"+today);
        System.out.println("last->"+ultimodia);
        while(today.before(ultimodia))
        {
            cal.setTime(today);
            System.out.println("sumo dia"+String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            cbCitaDia.addItem(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            cal.add(Calendar.DATE, 1);
            today=cal.getTime();
        }
        
        for(int i=0;i<horas.length;i++){
            cbCitaHora.addItem(horas[i]);
        }
        
        cbCitaMes.setSelectedIndex(cal.get(Calendar.MONTH));
    }
    
    
    
    public static void cargarCitas() {
        
        try{
            Session sesion=HibernateUtil.getSession();
            Iterator citasit = sesion.createCriteria(C_Cita.class).list().iterator();
            
            while(citasit.hasNext())
            {
                C_Cita cita=(C_Cita)citasit.next();
                
                Date fecha = cita.getFecha();
                
                if(fecha.compareTo(new Date())>0)
                   
                {
                    System.out.println("LA FECHA ES MAYOR QUE HOY!!");
                    Calendar c = Calendar.getInstance();
                    c.setTime(fecha);
                    
                    int dia=c.get(Calendar.DAY_OF_WEEK)-1;
                    String hora=c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
                    System.out.println("la cita es en un:"+dia+"para las "+hora);
                    
                    for (int i=0;i<horas.length;i++)
                        if(horas[i].compareToIgnoreCase(hora)==0){
                            citas[i][dia]=cita;
                            break;
                        }  
                }
                
            }
            sesion.close();
         
        }catch (Exception e) {
            
            System.out.println(e.getMessage());
        }
    }

    public static void cargarAnimales(){
        
        try{
            Session sesion=HibernateUtil.getSession();
            Iterator animales = sesion.createCriteria(C_Animal.class).list().iterator();
            
            while(animales.hasNext())
            {
                C_Animal animal=(C_Animal)animales.next();
                
                int id=animal.getId();
                String nombre=animal.getNombre();
                String tipo=animal.getTipo();
                String raza=animal.getRaza();
                String familiar= animal.getFamiliar().getNombre();
                
                //prueba
        /*
                try{
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
                Date fecha = parser.parse("2017-03-05 17:30:00");
                System.out.println("Creando cita para famliar:"+animal.getFamiliar().getNombre());
                C_Cita pruebacita=new C_Cita(fecha, animal.getFamiliar(), "el perro está enfermo");
                        System.out.println("tengo la cita para el dia"+pruebacita.getFecha());
                sesion.beginTransaction();
                sesion.save(pruebacita);
                sesion.getTransaction().commit();

                }catch(ParseException e){
                    System.out.println("MAL PARSE!");
                }
                //fin prueba
               */ 
                Object[] fila= {id, nombre,tipo,raza,familiar};
                modeloClientes.addRow(fila);
            }
            sesion.close();
         
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
        
        for(int i=0;i<paneles.size();i++){
            if (paneles.get(i).isVisible()){
                System.out.println("tengo un panel visible q es"+paneles.get(i));
                paneles.get(i).getParent().add(panel);
                //OJOO!! REVISAR exceso de paneles
                paneles.get(i).setVisible(false);
                panel.setSize(paneles.get(i).getSize());
                panel.setLocation(paneles.get(i).getLocation());
                panel.setVisible(true);
                
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
        btnFacturas = new javax.swing.JButton();
        btnInventario = new javax.swing.JButton();
        btnConfiguracion = new javax.swing.JButton();
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
        jPanel7 = new javax.swing.JPanel();
        lbNombre = new javax.swing.JLabel();
        rbHembra = new javax.swing.JRadioButton();
        lbTipo = new javax.swing.JLabel();
        rbMacho = new javax.swing.JRadioButton();
        cbTipo = new javax.swing.JComboBox<>();
        lbId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lbSexo = new javax.swing.JLabel();
        lbFamiliar = new javax.swing.JLabel();
        txtFamiliar = new javax.swing.JTextField();
        lbRaza = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        cbRaza = new javax.swing.JComboBox<>();
        rbgSexo = new javax.swing.ButtonGroup();
        panelVeterinarios = new javax.swing.JPanel();
        panelContactoClientes1 = new javax.swing.JPanel();
        btnMailCli1 = new javax.swing.JButton();
        btnContactoCli1 = new javax.swing.JButton();
        txtNombreVet = new javax.swing.JTextField();
        lbNombreVet = new javax.swing.JLabel();
        panelDatosClientes1 = new javax.swing.JPanel();
        btnEliminarCli1 = new javax.swing.JButton();
        btnEditarCli1 = new javax.swing.JButton();
        btnNuevoCli1 = new javax.swing.JButton();
        panelVerClientes1 = new javax.swing.JPanel();
        btnVerCli1 = new javax.swing.JButton();
        btnCitaCli1 = new javax.swing.JButton();
        scrollTabla1 = new javax.swing.JScrollPane();
        tablaVeterinarios = new javax.swing.JTable();
        panelCitas = new javax.swing.JPanel();
        panelContactoClientes2 = new javax.swing.JPanel();
        btnMailCli2 = new javax.swing.JButton();
        btnContactoCli2 = new javax.swing.JButton();
        panelDatosClientes2 = new javax.swing.JPanel();
        btnEliminarCli2 = new javax.swing.JButton();
        btnEditarCli2 = new javax.swing.JButton();
        btnNuevoCli2 = new javax.swing.JButton();
        txtNombreVet1 = new javax.swing.JTextField();
        panelVerClientes2 = new javax.swing.JPanel();
        btnVerCli2 = new javax.swing.JButton();
        btnCitaCli2 = new javax.swing.JButton();
        lbNombreVet1 = new javax.swing.JLabel();
        panelCitasList = new javax.swing.JScrollPane();
        tablaCitas = new javax.swing.JTable();
        panelFacturas = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lbNumFactura = new javax.swing.JLabel();
        txtNumFactura = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        lbFechaFactura = new javax.swing.JLabel();
        txtFechaFactura = new javax.swing.JTextField();
        panelControlesFacturas = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        panelInventario = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
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
        jPanel16 = new javax.swing.JPanel();
        lbCitaNom = new javax.swing.JLabel();
        lbCitaTipo = new javax.swing.JLabel();
        lbCitaRaza = new javax.swing.JLabel();
        lbCitaSexo = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtCitaAsunto = new javax.swing.JTextArea();
        lbCitaAsunto = new javax.swing.JLabel();
        txtCitaTipo = new javax.swing.JTextField();
        txtCitaRaza = new javax.swing.JTextField();
        txtCitaSexo = new javax.swing.JTextField();
        cbCitaNombreAni = new javax.swing.JComboBox<>();
        jPanel17 = new javax.swing.JPanel();
        cbCitaDia = new javax.swing.JComboBox<>();
        cbCitaHora = new javax.swing.JComboBox<>();
        lbResumenCita = new javax.swing.JLabel();
        cbCitaMes = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtResumenCita = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        btnCitaAceptar = new javax.swing.JButton();
        btnCitaCancel = new javax.swing.JButton();
        lbLogo = new javax.swing.JLabel();
        pbLoading = new javax.swing.JProgressBar();
        lbStatus = new javax.swing.JLabel();

        frameClientes.setPreferredSize(new java.awt.Dimension(912, 686));

        btnClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/pawprint.png"))); // NOI18N
        btnClientes.setText("Clientes");
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

        btnConfiguracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/ajustes.png"))); // NOI18N
        btnConfiguracion.setText("Configuración");
        btnConfiguracion.setMargin(new java.awt.Insets(2, 2, 2, 2));

        panelClientes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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
        btnNuevoCli.setToolTipText("");
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

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtrar Por"));

        lbNombre.setText("Nombre:");

        rbHembra.setText("Hembra");

        lbTipo.setText("Tipo");

        rbMacho.setText("Macho");

        cbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lbId.setText("Num Ficha:");

        lbSexo.setText("Sexo:");

        lbFamiliar.setText("Familiar:");

        lbRaza.setText("Raza");

        cbRaza.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbId)
                    .addComponent(lbNombre)
                    .addComponent(lbFamiliar))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNombre)
                    .addComponent(txtFamiliar)
                    .addComponent(txtId))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(lbTipo)
                        .addGap(18, 18, 18)
                        .addComponent(cbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbRaza)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbRaza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbSexo)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(rbHembra)
                        .addGap(8, 8, 8)
                        .addComponent(rbMacho)))
                .addGap(7, 7, 7))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbFamiliar)
                            .addComponent(txtFamiliar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbNombre)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lbTipo)
                                .addComponent(cbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lbRaza)
                                .addComponent(cbRaza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(3, 3, 3))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(lbSexo)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(rbHembra)
                                .addComponent(rbMacho)))))
                .addGap(4, 4, 4)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelClientesLayout.setVerticalGroup(
            panelClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDatosClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelVerClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelContactoClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout frameClientesLayout = new javax.swing.GroupLayout(frameClientes.getContentPane());
        frameClientes.getContentPane().setLayout(frameClientesLayout);
        frameClientesLayout.setHorizontalGroup(
            frameClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(frameClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnVeterinarios, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCitas, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfiguracion, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        frameClientesLayout.setVerticalGroup(
            frameClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frameClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(frameClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(frameClientesLayout.createSequentialGroup()
                        .addComponent(btnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCitas, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVeterinarios, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(btnConfiguracion, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 80, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panelVeterinarios.setPreferredSize(new java.awt.Dimension(735, 680));

        panelContactoClientes1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelContactoClientes1.setMaximumSize(new java.awt.Dimension(197, 67));
        panelContactoClientes1.setMinimumSize(new java.awt.Dimension(197, 67));
        panelContactoClientes1.setPreferredSize(new java.awt.Dimension(197, 67));

        btnMailCli1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/mensaje.png"))); // NOI18N
        btnMailCli1.setText("Enviar Mail");
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

        panelDatosClientes1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelDatosClientes1.setMaximumSize(new java.awt.Dimension(227, 67));
        panelDatosClientes1.setMinimumSize(new java.awt.Dimension(227, 67));

        btnEliminarCli1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo.png"))); // NOI18N
        btnEliminarCli1.setText("Eliminar");
        btnEliminarCli1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarCli1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnEditarCli1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo-nuevo.png"))); // NOI18N
        btnEditarCli1.setText("Editar");
        btnEditarCli1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditarCli1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnNuevoCli1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/anadir-pagina-nueva.png"))); // NOI18N
        btnNuevoCli1.setText("Nuevo");
        btnNuevoCli1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevoCli1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevoCli1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoCli1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatosClientes1Layout = new javax.swing.GroupLayout(panelDatosClientes1);
        panelDatosClientes1.setLayout(panelDatosClientes1Layout);
        panelDatosClientes1Layout.setHorizontalGroup(
            panelDatosClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosClientes1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNuevoCli1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditarCli1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminarCli1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDatosClientes1Layout.setVerticalGroup(
            panelDatosClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosClientes1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnNuevoCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditarCli1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelVerClientes1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelVerClientes1.setMaximumSize(new java.awt.Dimension(278, 67));
        panelVerClientes1.setMinimumSize(new java.awt.Dimension(278, 67));
        panelVerClientes1.setPreferredSize(new java.awt.Dimension(278, 67));

        btnVerCli1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/reanudar.png"))); // NOI18N
        btnVerCli1.setText("Últimos Diagnosticos");
        btnVerCli1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVerCli1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

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
                .addComponent(btnCitaCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelVerClientes1Layout.setVerticalGroup(
            panelVerClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelVerClientes1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelVerClientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCitaCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVerCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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
                    .addComponent(scrollTabla1, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                    .addGroup(panelVeterinariosLayout.createSequentialGroup()
                        .addComponent(lbNombreVet)
                        .addGap(18, 18, 18)
                        .addComponent(txtNombreVet, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelVeterinariosLayout.createSequentialGroup()
                        .addComponent(panelDatosClientes1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelVerClientes1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(7, 7, 7)
                        .addComponent(panelContactoClientes1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelVeterinariosLayout.setVerticalGroup(
            panelVeterinariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVeterinariosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelVeterinariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(panelContactoClientes1, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(panelVerClientes1, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(panelDatosClientes1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(panelVeterinariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNombreVet)
                    .addComponent(txtNombreVet))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollTabla1, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelCitas.setPreferredSize(new java.awt.Dimension(735, 680));

        panelContactoClientes2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelContactoClientes2.setMinimumSize(new java.awt.Dimension(197, 67));
        panelContactoClientes2.setPreferredSize(new java.awt.Dimension(197, 67));

        btnMailCli2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/mensaje.png"))); // NOI18N
        btnMailCli2.setText("Enviar Mail");
        btnMailCli2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMailCli2.setMaximumSize(new java.awt.Dimension(93, 23));
        btnMailCli2.setMinimumSize(new java.awt.Dimension(93, 23));
        btnMailCli2.setPreferredSize(new java.awt.Dimension(93, 23));
        btnMailCli2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnContactoCli2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/phone.png"))); // NOI18N
        btnContactoCli2.setText("Contacto");
        btnContactoCli2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContactoCli2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnContactoCli2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContactoCli2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelContactoClientes2Layout = new javax.swing.GroupLayout(panelContactoClientes2);
        panelContactoClientes2.setLayout(panelContactoClientes2Layout);
        panelContactoClientes2Layout.setHorizontalGroup(
            panelContactoClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContactoClientes2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMailCli2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnContactoCli2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelContactoClientes2Layout.setVerticalGroup(
            panelContactoClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelContactoClientes2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelContactoClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMailCli2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnContactoCli2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelDatosClientes2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelDatosClientes2.setMaximumSize(new java.awt.Dimension(227, 67));
        panelDatosClientes2.setMinimumSize(new java.awt.Dimension(227, 67));

        btnEliminarCli2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo.png"))); // NOI18N
        btnEliminarCli2.setText("Eliminar");
        btnEliminarCli2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarCli2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnEditarCli2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/archivo-nuevo.png"))); // NOI18N
        btnEditarCli2.setText("Editar");
        btnEditarCli2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditarCli2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnNuevoCli2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/anadir-pagina-nueva.png"))); // NOI18N
        btnNuevoCli2.setText("Nuevo");
        btnNuevoCli2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevoCli2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevoCli2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoCli2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatosClientes2Layout = new javax.swing.GroupLayout(panelDatosClientes2);
        panelDatosClientes2.setLayout(panelDatosClientes2Layout);
        panelDatosClientes2Layout.setHorizontalGroup(
            panelDatosClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosClientes2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNuevoCli2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditarCli2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminarCli2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDatosClientes2Layout.setVerticalGroup(
            panelDatosClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosClientes2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnNuevoCli2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditarCli2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarCli2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelVerClientes2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelVerClientes2.setMaximumSize(new java.awt.Dimension(278, 47));
        panelVerClientes2.setMinimumSize(new java.awt.Dimension(278, 47));

        btnVerCli2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/reanudar.png"))); // NOI18N
        btnVerCli2.setText("Últimos Diagnosticos");
        btnVerCli2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVerCli2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnCitaCli2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/calendario green.png"))); // NOI18N
        btnCitaCli2.setText("Crear Cita");
        btnCitaCli2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCitaCli2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout panelVerClientes2Layout = new javax.swing.GroupLayout(panelVerClientes2);
        panelVerClientes2.setLayout(panelVerClientes2Layout);
        panelVerClientes2Layout.setHorizontalGroup(
            panelVerClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVerClientes2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnVerCli2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCitaCli2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelVerClientes2Layout.setVerticalGroup(
            panelVerClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelVerClientes2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelVerClientes2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCitaCli2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVerCli2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        lbNombreVet1.setText("Nombre:");

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

        javax.swing.GroupLayout panelCitasLayout = new javax.swing.GroupLayout(panelCitas);
        panelCitas.setLayout(panelCitasLayout);
        panelCitasLayout.setHorizontalGroup(
            panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCitasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCitasList, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                    .addGroup(panelCitasLayout.createSequentialGroup()
                        .addComponent(lbNombreVet1)
                        .addGap(18, 18, 18)
                        .addComponent(txtNombreVet1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelCitasLayout.createSequentialGroup()
                        .addComponent(panelDatosClientes2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelVerClientes2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelContactoClientes2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelCitasLayout.setVerticalGroup(
            panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCitasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDatosClientes2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelVerClientes2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelContactoClientes2, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(panelCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNombreVet1)
                    .addComponent(txtNombreVet1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCitasList, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelFacturas.setPreferredSize(new java.awt.Dimension(735, 686));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 168, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 72, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        lbNumFactura.setText("FACTURA Nº:");

        txtNumFactura.setText(" ");

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        lbFechaFactura.setText("Fecha:");

        txtFechaFactura.setText(" ");

        panelControlesFacturas.setBorder(javax.swing.BorderFactory.createTitledBorder("Controles"));

        jButton1.setText("Añadir");

        jButton2.setText("Eliminar");

        javax.swing.GroupLayout panelControlesFacturasLayout = new javax.swing.GroupLayout(panelControlesFacturas);
        panelControlesFacturas.setLayout(panelControlesFacturasLayout);
        panelControlesFacturasLayout.setHorizontalGroup(
            panelControlesFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelControlesFacturasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelControlesFacturasLayout.setVerticalGroup(
            panelControlesFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelControlesFacturasLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(panelControlesFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jLabel2.setText("Total:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("654.5 €");

        jLabel5.setText("SubTotal:");

        jLabel6.setText("Descuento:");

        jLabel7.setText("Impuestos:");

        jLabel8.setText("670 y tantos");

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Receptor"));

        jLabel9.setText("Nombre Y apell del receptor o num Factura");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(191, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel10.setText("0");

        jLabel11.setText("25.12");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jCheckBox1.setText("Incluir Consulta");

        javax.swing.GroupLayout panelFacturasLayout = new javax.swing.GroupLayout(panelFacturas);
        panelFacturas.setLayout(panelFacturasLayout);
        panelFacturasLayout.setHorizontalGroup(
            panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFacturasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelControlesFacturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelFacturasLayout.createSequentialGroup()
                        .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFacturasLayout.createSequentialGroup()
                                .addComponent(lbFechaFactura)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtFechaFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFacturasLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jCheckBox1))
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFacturasLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lbNumFactura)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNumFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)))
                .addContainerGap())
        );
        panelFacturasLayout.setVerticalGroup(
            panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFacturasLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNumFactura)
                    .addComponent(txtNumFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbFechaFactura)
                    .addComponent(txtFechaFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addGap(10, 10, 10)
                .addGroup(panelFacturasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelControlesFacturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        javax.swing.GroupLayout panelInventarioLayout = new javax.swing.GroupLayout(panelInventario);
        panelInventario.setLayout(panelInventarioLayout);
        panelInventarioLayout.setHorizontalGroup(
            panelInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInventarioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(516, Short.MAX_VALUE))
        );
        panelInventarioLayout.setVerticalGroup(
            panelInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInventarioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
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
                    .addComponent(txtMailFami)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDatosAnimalLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(24, 24, 24)
                        .addComponent(newRbHembra)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(newRbMacho))
                    .addGroup(panelDatosAnimalLayout.createSequentialGroup()
                        .addComponent(lbTipoAni)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbTipoAni, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbRazaAni)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbRazaAni, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelDatosAnimalLayout.setVerticalGroup(
            panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosAnimalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDatosAnimalLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newRbHembra)
                            .addComponent(newRbMacho)
                            .addComponent(jLabel3)))
                    .addGroup(panelDatosAnimalLayout.createSequentialGroup()
                        .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbNombreAni)
                            .addComponent(cbRazaAni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbTipoAni)
                            .addComponent(lbRazaAni)
                            .addComponent(cbTipoAni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombreCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelDatosAnimalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtChipidCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(19, Short.MAX_VALUE))
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
                        .addComponent(btnAceptar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelar)))
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
                .addGap(28, 28, 28)
                .addGroup(dialogEditClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAceptar)
                    .addComponent(btnCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDatosFamiliar5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Familiar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos Contacto"));

        lbMailFami5.setText("E-mail:");

        lbTlfFami5.setText("Teléfono:");

        txtCitaTlfFami.setText(" ");

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

        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lbCitaNom.setText("Nombre:");

        lbCitaTipo.setText("Tipo:");

        lbCitaRaza.setText("Raza:");

        lbCitaSexo.setText("Sexo:");

        txtCitaAsunto.setColumns(20);
        txtCitaAsunto.setRows(5);
        jScrollPane5.setViewportView(txtCitaAsunto);

        lbCitaAsunto.setText("Asunto:");

        cbCitaNombreAni.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbCitaNom)
                            .addComponent(lbCitaSexo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCitaSexo)
                            .addComponent(cbCitaNombreAni, 0, 83, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbCitaTipo)
                            .addComponent(lbCitaRaza))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCitaRaza)
                            .addComponent(txtCitaTipo)))
                    .addComponent(lbCitaAsunto))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCitaNom)
                    .addComponent(lbCitaTipo)
                    .addComponent(txtCitaTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbCitaNombreAni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCitaRaza)
                    .addComponent(lbCitaSexo)
                    .addComponent(txtCitaRaza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCitaSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        lbResumenCita.setText("Resumen Cita:");

        cbCitaMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));

        txtResumenCita.setEditable(false);
        txtResumenCita.setColumns(20);
        txtResumenCita.setRows(5);
        jScrollPane4.setViewportView(txtResumenCita);

        jLabel12.setText("Día:");

        jLabel13.setText("Hora:");

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
                                .addComponent(cbCitaDia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(87, 87, 87)))
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(cbCitaHora, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(lbResumenCita)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cbCitaMes, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbCitaMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelDatosCitaLayout.setVerticalGroup(
            panelDatosCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosCitaLayout.createSequentialGroup()
                .addGroup(panelDatosCitaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        btnCitaAceptar.setText("Aceptar");

        btnCitaCancel.setText("Cancelar");

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
                .addGap(26, 26, 26)
                .addComponent(btnCitaAceptar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGap(18, 18, 18)
                .addGroup(dialogEditCitasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCitaAceptar)
                    .addComponent(btnCitaCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        lbLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/drawable/vet-logo.gif"))); // NOI18N

        lbStatus.setText("Estoy haciendo cosas");

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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(pbLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(121, 121, 121))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lbStatus)
                                .addGap(277, 277, 277))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pbLoading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbStatus)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevoCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoCliActionPerformed
        
        dialogEditClientes.setVisible(true);
        dialogEditClientes.setModal(true);
        cargarFamiliares();
        
        //frameClientes.disable();
        
        
    }//GEN-LAST:event_btnNuevoCliActionPerformed

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed

        String nombreAni, tipo, raza, comentario;
        Date fecha_nac;
        float peso;
        char sexo;
        //boolean[] vacunas = new boolean[6];
        
        nombreAni=txtNombreCli.getText();
        tipo=cbTipoAni.getSelectedItem().toString();
        raza=cbRazaAni.getSelectedItem().toString();
        fecha_nac=new Date();
        
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        
        try{
        Date date = parser.parse(txtFechaCli.getText());
        
        }catch (ParseException e){ 
            System.out.println("NO!!!!");
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
        
        /*
        for(int i=0;i<panelVacunas.getComponentCount();i++){
            if ( ((JCheckBox)panelVacunas.getComponent(i)).isSelected() ){
                vacuna=((JCheckBox)panelVacunas.getComponent(i)).getText();
                C_Medicamento medicamento=(C_Medicamento)sesion.createQuery("FROM POJOS.C_Medicamento m WHERE m.nombre='"+vacuna+"' AND m.tipo='vacuna' ").uniqueResult();
                animal.getVacunas().add(medicamento);//OJO!!!
            }
        }
        */

        Guardar.guardarAnimal(animal);
        
        dialogEditClientes.dispose();
        
        
        /*
        /////
        for(int i=0;i<panelVacunas.getComponentCount();i++){
            if ( ((JCheckBox)panelVacunas.getComponent(i)).isSelected() )
                animal.getVacunas().add(i);//OJO!!!
        }
        */
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnNuevoCli2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoCli2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnNuevoCli2ActionPerformed

    private void btnCitasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCitasActionPerformed
        
        cambiarPanel(panelCitas);
        /*
        Date today=new Date();
        Session sesion=HibernateUtil.getSession();
        Iterator citas=sesion.createQuery("FROM POJOS.C_Cita c WHERE c.fecha > '"+today+"'").list().iterator();
        List citasDiaActual= new LinkedList<C_Cita>();
        while(citas.hasNext()){
            System.out.println("tengo citas");
            C_Cita cita =(C_Cita) citas.next();
            
        }*/
        Calendar calendar=Calendar.getInstance();
        String[] columnasCitas=new String[7];
        switch(calendar.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY:
                columnasCitas=new String[]{"   ", "Lunes ","Martes"," Miercoles", "Jueves" , "Viernes" , "Sabado"};
                break;
            case Calendar.TUESDAY:
                columnasCitas=new String[]{"   ","Martes"," Miercoles", "Jueves" , "Viernes" , "Sabado", "Lunes "};
                break;
            case Calendar.WEDNESDAY:
                columnasCitas=new String[]{"   "," Miercoles", "Jueves" , "Viernes" , "Sabado", "Lunes ","Martes"};
                break;
            case Calendar.THURSDAY:
                columnasCitas=new String[]{"   ", "Jueves" , "Viernes" , "Sabado", "Lunes ","Martes"," Miercoles"};
                break;
            case Calendar.FRIDAY:
                columnasCitas=new String[]{"   ", "Viernes" , "Sabado", "Lunes ","Martes"," Miercoles", "Jueves"};
                break;
            case Calendar.SATURDAY:
                columnasCitas=new String[]{"   " , "Sabado", "Lunes ","Martes"," Miercoles", "Jueves" , "Viernes"};
                break;
            case Calendar.SUNDAY:
                columnasCitas=new String[]{"   ", "Lunes ","Martes"," Miercoles", "Jueves" , "Viernes" , "Sabado"};
                break;
            
        }
        
        
        
        
//        String[] filaCitas = {"9:00","9:15","9:30","9:45","10:00","10:15","10:30","10:45",
//                              "11:00","11:15","11:30","11:45","12:00","12:15","12:30","12:45",
//                              "13:00","13:15","13:30","13:45","     ",
//                              "17:00","17:15","17:30","17:45","18:00","18:15","18:30","18:45",
//                              "19:00","19:15","19:30","19:45","20:00","20:15","20:30","20:45"};
        
        for (int i=0;i<horas.length;i++){
            citas[i][0]=horas[i];
        }
        cargarCitas();
        DefaultTableModel modeloCitas=new DefaultTableModel(citas, columnasCitas){
            @Override
            public boolean isCellEditable(int row, int column) {
                //no quiero que se edite nada!!! 
                return false;
            }
        };
        
        //configuracion del renderer
        DefaultTableCellRenderer myRenderer = new DefaultTableCellRenderer();
        myRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        //myRenderer.setFont(getFont().deriveFont(Font.BOLD, 18));
        myRenderer.setFont(new Font("Tahoma", Font.BOLD, 18));////<<<<ESTO NO VA!!! ARREGLAAAAAAAAAAR!!!!!!!!!!!!!!!!!!!
        //tablaCitas.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
        
        
        //fin renderer
        tablaCitas.setCellSelectionEnabled(true);
        tablaCitas.setModel(modeloCitas);
        tablaCitas.setRowHeight(40);
        
        //tablaCitas.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 13));
        tablaCitas.getTableHeader().setDefaultRenderer(myRenderer);
        tablaCitas.getColumnModel().getColumn(0).setCellRenderer( myRenderer );
        
    }//GEN-LAST:event_btnCitasActionPerformed

    
    private void btnFacturasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturasActionPerformed
        cambiarPanel(panelFacturas);
    }//GEN-LAST:event_btnFacturasActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        cambiarPanel(panelClientes);
    }//GEN-LAST:event_btnClientesActionPerformed

    private void btnVeterinariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVeterinariosActionPerformed
        cambiarPanel(panelVeterinarios);
    }//GEN-LAST:event_btnVeterinariosActionPerformed

    private void btnInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarioActionPerformed
        cambiarPanel(panelInventario);
    }//GEN-LAST:event_btnInventarioActionPerformed

    private void btnEditarCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarCliActionPerformed
        //PANEL CLIENTES BOTON EDITAR CLIENTE
        if(tablaClientes.getSelectedRowCount()>0)
        {
            System.out.println("TENGO SELECTED");
            int id=(int)modeloClientes.getValueAt(tablaClientes.getSelectedRow(), 0);
            System.out.println("tengo el ID ---> "+id);
            Session sesion=HibernateUtil.getSession();
            
            
            C_Animal animal=(C_Animal)sesion.createQuery("FROM POJOS.C_Animal a WHERE a.id='"+id+"'").uniqueResult();
            
            dialogEditClientes.setVisible(true);
            dialogEditClientes.setModal(true);
            cargarFamiliares();
            
            txtNombreCli.setText(animal.getNombre());
            txtChipidCli.setText(animal.getId_chip());
            cbTipoAni.setSelectedItem(animal.getTipo());
            cbRazaAni.setSelectedItem(animal.getRaza());
            
            txtFechaCli.setText(animal.getFecha_nac().toString());
            txtPesoCli.setText(""+animal.getPeso());
            txtComentarioCli.setText(animal.getComentario());
            
            //--familiar--
            
            cbDniFami.setSelectedItem(animal.getFamiliar().getDni());
            txtNombreFami.setText(animal.getFamiliar().getNombre());
            txtTlfFami.setText(animal.getFamiliar().getTelefono());
            txtMailFami.setText(animal.getFamiliar().getEmail());
            txtDireFami.setText( ((C_Familiar)animal.getFamiliar()).getDireccion());
        
        }
        else
            System.out.println("NO TENGO SELECTED    ---"+tablaClientes.getSelectedRowCount());
        
    }//GEN-LAST:event_btnEditarCliActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        
        dialogEditClientes.dispose();
        
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void cbDniFamiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDniFamiActionPerformed
        //DIALOG_CLIENTES COMBODNI CLICK
        try{
        String dni= cbDniFami.getSelectedItem().toString();
        
        Session sesion=HibernateUtil.getSession();
        C_Persona familiar = (C_Familiar)sesion.createQuery("FROM POJOS.C_Persona f WHERE f.dni='"+dni+"'").uniqueResult();
        
        if(familiar != null){
            txtTlfFami.setText(familiar.getTelefono());
            txtDireFami.setText(((C_Familiar)familiar).getDireccion());
            txtMailFami.setText(familiar.getEmail());
            txtNombreFami.setText(familiar.getNombre());
            
        }
      }catch(Exception e){}
        
    }//GEN-LAST:event_cbDniFamiActionPerformed

    private void btnNuevoCli1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoCli1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnNuevoCli1ActionPerformed

    private void btnMailCli1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMailCli1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMailCli1ActionPerformed

    private void btnContactoCli2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContactoCli2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnContactoCli2ActionPerformed

    private void btnEliminarCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarCliActionPerformed
        //PANEL CLIENTES BOTON ELIMINAR CLIENTE
        if(tablaClientes.getSelectedRowCount()>0)
        {
            System.out.println("TENGO SELECTED");
            int id=(int)modeloClientes.getValueAt(tablaClientes.getSelectedRow(), 0);
            System.out.println("tengo el ID ---> "+id);
            //Session sesion=HibernateUtil.getSession();
            
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "¿Está Seguro que desea eliminar este registro?","Warning",dialogButton);
            
            if(dialogResult == JOptionPane.YES_OPTION){
            
                Eliminar.EliminarAnimal(id);
                System.out.println("Eliminado de la BD");
                
                modeloClientes.removeRow(tablaClientes.getSelectedRow());
                System.out.println("eliminado de la tabla");
                
            }
            
        
        }
        else
            System.out.println("NO TENGO SELECTED    ---"+tablaClientes.getSelectedRowCount());
    }//GEN-LAST:event_btnEliminarCliActionPerformed

    private void cbCitaDniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCitaDniActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbCitaDniActionPerformed

    private void btnCitaCliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCitaCliActionPerformed
        //PANEL CLIENTES BOTON NUEVA CITA
        if(tablaClientes.getSelectedRowCount()>0)
        {
            System.out.println("TENGO SELECTED");
            int id=(int)modeloClientes.getValueAt(tablaClientes.getSelectedRow(), 0);
            System.out.println("tengo el ID ---> "+id);
            Session sesion=HibernateUtil.getSession();
            
            
            C_Animal animal=(C_Animal)sesion.createQuery("FROM POJOS.C_Animal a WHERE a.id='"+id+"'").uniqueResult();
            
            dialogEditCitas.setVisible(true);
            dialogEditCitas.setModal(true);
            
            txtCitaNombreFami.setText(animal.getFamiliar().getNombre());
            txtCitaTlfFami.setText(animal.getFamiliar().getTelefono());
            cbCitaDni.setSelectedItem(animal.getFamiliar().getDni());
            cbCitaNombreAni.setSelectedItem(animal.getNombre());
            txtCitaMailFami.setText(animal.getFamiliar().getEmail());
            txtCitaTipo.setText(animal.getTipo());
            
            switch(animal.getSexo()){
                case 'M':
                    txtCitaSexo.setText("Macho");
                    break;
                case 'H':
                    txtCitaSexo.setText("Hembra");
                    break;
            }
            txtCitaRaza.setText(animal.getRaza());
            
            cargarCitasLibres();
        
        }
        else
            System.out.println("NO TENGO SELECTED    ---"+tablaClientes.getSelectedRowCount());
    }//GEN-LAST:event_btnCitaCliActionPerformed

    private void cbCitaDiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCitaDiaActionPerformed
        // DIALOG_CITAS COMBO HORAS LIBRES
        
        try{
            Session sesion=HibernateUtil.getSession();
            
            Iterator citasit = sesion.createCriteria(C_Cita.class).list().iterator();
            
            while(citasit.hasNext())
            {
                C_Cita cita=(C_Cita)citasit.next();
                
                Date fecha = cita.getFecha();
                Calendar cal = Calendar.getInstance();
                cal.setTime(fecha);
                Calendar day2 = Calendar.getInstance();
                day2.set(Calendar.DATE, Integer.parseInt(cbCitaDia.getSelectedItem().toString()));
                
                System.out.println("comparo:"+cal.getTime()+"  con  "+day2.getTime());
                if(cal.get(Calendar.DAY_OF_MONTH)== day2.get(Calendar.DAY_OF_MONTH))
                   
                {
                    System.out.println("coincide!!");
                    //cal.setTime(fecha);
                    
                    String hora=cal.getTime().getHours()+":"+cal.getTime().getMinutes();
                    System.out.println("elimino la hora"+hora);

                    cbCitaHora.removeItem(hora);
                    System.out.println("cita eliminada");
                }
                else
                    System.out.println("no coinciden");
                
            }
            sesion.close();
         
        }catch (Exception e) {
            
            System.out.println(e.getMessage());
        }
        
        
        
        
    }//GEN-LAST:event_cbCitaDiaActionPerformed

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
                new MainWindow().setVisible(true);
                
            }
        });
        Session sesion=HibernateUtil.getSession();
                sesion.close();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelDatosMed;
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCitaAceptar;
    private javax.swing.JButton btnCitaCancel;
    private javax.swing.JButton btnCitaCli;
    private javax.swing.JButton btnCitaCli1;
    private javax.swing.JButton btnCitaCli2;
    private javax.swing.JButton btnCitas;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnConfiguracion;
    private javax.swing.JButton btnContactoCli;
    private javax.swing.JButton btnContactoCli1;
    private javax.swing.JButton btnContactoCli2;
    private javax.swing.JButton btnEditarCli;
    private javax.swing.JButton btnEditarCli1;
    private javax.swing.JButton btnEditarCli2;
    private javax.swing.JButton btnEliminarCli;
    private javax.swing.JButton btnEliminarCli1;
    private javax.swing.JButton btnEliminarCli2;
    private javax.swing.JButton btnFacturas;
    private javax.swing.JButton btnInventario;
    private javax.swing.JButton btnMailCli;
    private javax.swing.JButton btnMailCli1;
    private javax.swing.JButton btnMailCli2;
    private javax.swing.JButton btnNuevoCli;
    private javax.swing.JButton btnNuevoCli1;
    private javax.swing.JButton btnNuevoCli2;
    private javax.swing.JButton btnVerCli;
    private javax.swing.JButton btnVerCli1;
    private javax.swing.JButton btnVerCli2;
    private javax.swing.JButton btnVeterinarios;
    private static javax.swing.JComboBox<String> cbCitaDia;
    private static javax.swing.JComboBox<String> cbCitaDni;
    private static javax.swing.JComboBox<String> cbCitaHora;
    private static javax.swing.JComboBox<String> cbCitaMes;
    private javax.swing.JComboBox<String> cbCitaNombreAni;
    private static javax.swing.JComboBox<String> cbDniFami;
    private javax.swing.JComboBox<String> cbRaza;
    private javax.swing.JComboBox<String> cbRazaAni;
    private javax.swing.JComboBox<String> cbTipo;
    private javax.swing.JComboBox<String> cbTipoAni;
    private javax.swing.JCheckBox cbVacuna1;
    private javax.swing.JCheckBox cbVacuna2;
    private javax.swing.JCheckBox cbVacuna3;
    private javax.swing.JCheckBox cbVacuna4;
    private javax.swing.JCheckBox cbVacuna5;
    private javax.swing.JCheckBox cbVacuna6;
    private javax.swing.JDialog dialogEditCitas;
    public javax.swing.JDialog dialogEditClientes;
    private javax.swing.JFrame frameClientes;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JPanel jPanel16;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lbCitaAsunto;
    private javax.swing.JLabel lbCitaNom;
    private javax.swing.JLabel lbCitaRaza;
    private javax.swing.JLabel lbCitaSexo;
    private javax.swing.JLabel lbCitaTipo;
    private javax.swing.JLabel lbComentarioCli;
    private javax.swing.JLabel lbDireFami;
    private javax.swing.JLabel lbDireFami1;
    private javax.swing.JLabel lbDireFami2;
    private javax.swing.JLabel lbDireFami3;
    private javax.swing.JLabel lbDniCli;
    private javax.swing.JLabel lbDniCli2;
    private javax.swing.JLabel lbFamiliar;
    private javax.swing.JLabel lbFechaFactura;
    private javax.swing.JLabel lbFecha_nacAni;
    private javax.swing.JLabel lbId;
    private javax.swing.JLabel lbLogo;
    private javax.swing.JLabel lbMailFami;
    private javax.swing.JLabel lbMailFami1;
    private javax.swing.JLabel lbMailFami2;
    private javax.swing.JLabel lbMailFami3;
    private javax.swing.JLabel lbMailFami5;
    private javax.swing.JLabel lbNombre;
    private javax.swing.JLabel lbNombreAni;
    private javax.swing.JLabel lbNombreFami;
    private javax.swing.JLabel lbNombreFami2;
    private javax.swing.JLabel lbNombreVet;
    private javax.swing.JLabel lbNombreVet1;
    private javax.swing.JLabel lbNumFactura;
    private javax.swing.JLabel lbPesoCli;
    private javax.swing.JLabel lbRaza;
    private javax.swing.JLabel lbRazaAni;
    private javax.swing.JLabel lbResumenCita;
    private javax.swing.JLabel lbSexo;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTipo;
    private javax.swing.JLabel lbTipoAni;
    private javax.swing.JLabel lbTlfFami;
    private javax.swing.JLabel lbTlfFami1;
    private javax.swing.JLabel lbTlfFami2;
    private javax.swing.JLabel lbTlfFami3;
    private javax.swing.JLabel lbTlfFami5;
    private javax.swing.JRadioButton newRbHembra;
    private javax.swing.JRadioButton newRbMacho;
    private javax.swing.JPanel panelCitas;
    private javax.swing.JScrollPane panelCitasList;
    private javax.swing.JPanel panelClientes;
    private javax.swing.JPanel panelContactoClientes;
    private javax.swing.JPanel panelContactoClientes1;
    private javax.swing.JPanel panelContactoClientes2;
    private javax.swing.JPanel panelControlesFacturas;
    private javax.swing.JPanel panelDatosAnimal;
    private javax.swing.JPanel panelDatosCita;
    private javax.swing.JPanel panelDatosClientes;
    private javax.swing.JPanel panelDatosClientes1;
    private javax.swing.JPanel panelDatosClientes2;
    private javax.swing.JPanel panelDatosFamiliar;
    private javax.swing.JPanel panelDatosFamiliar1;
    private javax.swing.JPanel panelDatosFamiliar2;
    private javax.swing.JPanel panelDatosFamiliar3;
    private javax.swing.JPanel panelDatosFamiliar5;
    private javax.swing.JPanel panelFacturas;
    private javax.swing.JPanel panelInventario;
    private javax.swing.JPanel panelVacunas;
    private javax.swing.JPanel panelVerClientes;
    private javax.swing.JPanel panelVerClientes1;
    private javax.swing.JPanel panelVerClientes2;
    private javax.swing.JPanel panelVeterinarios;
    private javax.swing.JProgressBar pbLoading;
    private javax.swing.JRadioButton rbHembra;
    private javax.swing.JRadioButton rbMacho;
    private javax.swing.ButtonGroup rbgSexo;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JScrollPane scrollTabla1;
    private javax.swing.JTable tablaCitas;
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTable tablaVeterinarios;
    private javax.swing.JTextField txtChipidCli;
    private javax.swing.JTextArea txtCitaAsunto;
    private javax.swing.JTextField txtCitaMailFami;
    private javax.swing.JTextField txtCitaNombreFami;
    private javax.swing.JTextField txtCitaRaza;
    private javax.swing.JTextField txtCitaSexo;
    private javax.swing.JTextField txtCitaTipo;
    private javax.swing.JTextField txtCitaTlfFami;
    private javax.swing.JTextArea txtComentarioCli;
    private javax.swing.JTextField txtDireFami;
    private javax.swing.JTextField txtDireFami1;
    private javax.swing.JTextField txtDireFami2;
    private javax.swing.JTextField txtDireFami3;
    private javax.swing.JTextField txtFamiliar;
    private javax.swing.JTextField txtFechaCli;
    private javax.swing.JTextField txtFechaFactura;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtMailFami;
    private javax.swing.JTextField txtMailFami1;
    private javax.swing.JTextField txtMailFami2;
    private javax.swing.JTextField txtMailFami3;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNombreCli;
    private javax.swing.JTextField txtNombreFami;
    private javax.swing.JTextField txtNombreVet;
    private javax.swing.JTextField txtNombreVet1;
    private javax.swing.JTextField txtNumFactura;
    private javax.swing.JTextField txtPesoCli;
    private javax.swing.JTextArea txtResumenCita;
    private javax.swing.JTextField txtTlfFami;
    private javax.swing.JTextField txtTlfFami1;
    private javax.swing.JTextField txtTlfFami2;
    private javax.swing.JTextField txtTlfFami3;
    // End of variables declaration//GEN-END:variables
}

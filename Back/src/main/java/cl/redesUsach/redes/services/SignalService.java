package cl.redesUsach.redes.services;

import cl.redesUsach.redes.models.Lugar;
import cl.redesUsach.redes.models.Signal;
import cl.redesUsach.redes.repositories.LugarRepository;
import cl.redesUsach.redes.repositories.SignalRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@CrossOrigin
@RestController
@RequestMapping("/signals")
public class SignalService {
	@Autowired
	SignalRepository signalRepository;
	@Autowired
	LugarRepository lugarRepository;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Iterable<Signal> getAllSignals() {
		return signalRepository.findAll();
	}

	@GetMapping("/{calidad}/{lugar}")
	public Iterable<Signal> getEspecificSignal(@PathVariable("calidad") String calidad,
			@PathVariable("lugar") String lugar) {
		Iterable<Signal> señales;
		if (lugar.equals("todos")) {
			señales = signalRepository.findAll();
		} else {
			señales = signalRepository.findAllByLugar(lugar);
		}
		List<Signal> seleccionadas = new ArrayList<Signal>();
		for (Signal señal : señales) {
			if (señal.getEstado() != null && señal.getEstado().equals(calidad)) {
				seleccionadas.add(señal);
			}
		}
		return seleccionadas;
	}

	/*
	 * @RequestMapping(value= "/fecha/{fecha}",method = RequestMethod.GET)
	 * 
	 * @ResponseBody public String getSignalsByDate(@PathVariable("fecha") String
	 * fecha ){ SimpleDateFormat formatter = new
	 * SimpleDateFormat("dd-MM-yyy HH:mm"); try{ Date temp=formatter.parse(fecha);
	 * return signalRepository.findAllByFecha(temp).toString();
	 * }catch(ParseException e){ return "Error "+fecha; }
	 * 
	 * }
	 */

	@GetMapping("/fecha")
	public Map<String, Object> getSignalsByDate() throws JSONException, ParseException {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		Date actual = cal.getTime();

		cal.add(Calendar.DAY_OF_YEAR, -30);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date anterior = cal.getTime();

		List<Signal> signals = signalRepository.findAll();
		List<Signal> signalsFecha = new ArrayList<Signal>();
		List<Signal> signalMañana = new ArrayList<Signal>();
		List<Signal> signalTarde = new ArrayList<Signal>();
		List<Signal> signalNoche = new ArrayList<Signal>();

		for (Signal signal : signals) {

			if (signal.getFecha().after(anterior) && signal.getFecha().before(actual)) {
				signalsFecha.add(signal);
			}
		}

		for (Signal signal : signalsFecha) {

			cal.setTime(signal.getFecha());
			int hour = cal.get(Calendar.HOUR_OF_DAY);

			if (hour >= 6 && hour <= 12) {

				signalMañana.add(signal);
			} else if (hour > 12 && hour <= 18) {

				signalTarde.add(signal);
			} else {
				signalNoche.add(signal);
			}
		}

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("mañana", signalMañana);
		body.put("tarde", signalTarde);
		body.put("noche", signalNoche);
		return body;
	}

	@PostMapping("/fechas")
	public Map<String, Object> getSignalsByIntervalo(@RequestBody String json)
			throws JSONException, ParseException, CloneNotSupportedException {

		JSONObject response = new JSONObject(json);
		String inicio = response.getString("fechaInicio");
		String termino = response.getString("fechaTermino");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date fechaInicio = df.parse(inicio);
		Date fechaTermino = df.parse(termino);
		System.out.println(fechaInicio + "----" + fechaTermino);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		Date actual = cal.getTime();

		cal.add(Calendar.DAY_OF_YEAR, -30);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date anterior = cal.getTime();
		// falta definir las fechas
		List<Signal> signals = signalRepository.findAll();
		List<Signal> signalsFecha = new ArrayList<Signal>();
		ArrayList<Signal> signalMañana = new ArrayList<Signal>();
		ArrayList<Signal> signalTarde = new ArrayList<Signal>();
		ArrayList<Signal> signalNoche = new ArrayList<Signal>();

		for (Signal signal : signals) {
			Date fechaAdaptada = df.parse(df.format(signal.getFecha()));
			System.out.println(fechaAdaptada);
			System.out.println("*****" + signal.getFecha().compareTo(fechaInicio));
			System.out.println("****" + signal.getFecha().compareTo(fechaTermino));

			if ((fechaAdaptada.compareTo(fechaInicio) == 0 || fechaAdaptada.compareTo(fechaInicio) == 1)
					&& (fechaAdaptada.compareTo(fechaTermino) == 0 || fechaAdaptada.compareTo(fechaTermino) == -1)) {
				signalsFecha.add(signal);
				System.out.println("****************agregando****************************");
			}
		}

		for (Signal signal : signalsFecha) {

			cal.setTime(signal.getFecha());
			int hour = cal.get(Calendar.HOUR_OF_DAY);

			if (hour >= 6 && hour <= 12) {

				signalMañana.add(signal);
				System.out.println("agregando en mañana");
			} else if (hour > 12 && hour <= 18) {

				signalTarde.add(signal);
				System.out.println("agregando en tarde");
			} else {
				signalNoche.add(signal);
				System.out.println("agregando en noche");
			}
		}

		signalMañana = Signal.promediar(signalMañana);
		// signalMañana=Signal.asignarPesoClon(signalMañana);

		signalTarde = Signal.promediar(signalTarde);
		// signalTarde=Signal.asignarPesoClon(signalTarde);

		signalNoche = Signal.promediar(signalNoche);
		// signalNoche=Signal.asignarPesoClon(signalNoche);

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("mañana", signalMañana);
		body.put("tarde", signalTarde);
		body.put("noche", signalNoche);
		return body;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public Signal create(@RequestBody Signal resource) {
		System.out.println(resource.getFecha());
		return signalRepository.save(resource);
	}

	@GetMapping("/send/{lugar}")
	@Transactional
	public HttpStatus sendEmail(@PathVariable("lugar") String lugar) throws FileNotFoundException, DocumentException {

		Iterable<Signal> señales;
		List<Lugar> lugares;

		if (lugar.equals("all")) {
			// Estadistica considerando todos los lugares
			señales = signalRepository.findAll();
			lugares = lugarRepository.findAll();

			//
			File archivo = new File("iTextHelloWorld.pdf");
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(archivo));

			document.open();

			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);
			//

			for (Lugar ubicacion : lugares) {
				Iterable<Signal> señal1;
				señal1 = signalRepository.findAllByLugar(ubicacion.getNombre());

				Double velocidadSumatoria = 0.0;
				Double intensidadSumatoria = 0.0;
				Double contador = 0.0;

				for (Signal señal : señal1) {
					if (señal.getEstado() != null) {
						velocidadSumatoria = velocidadSumatoria + Double.parseDouble(señal.getVelocidad());
						intensidadSumatoria = intensidadSumatoria + Double.parseDouble(señal.getIntensidad());
						contador++;
					}
				}
				Double velocidadPromedio = velocidadSumatoria / contador;
				Double intensidadPromedio = intensidadSumatoria / contador;

				HashMap<String, Double> resultado = new HashMap<>();
				resultado.put("velocidadPromedio", velocidadPromedio);
				resultado.put("intensidadPromedio", intensidadPromedio);

				// Añadir a PDF
				document.add(new Paragraph("Lugar: " + ubicacion.getNombre(), font));
				document.add(
						new Paragraph("Velocidad Promedio: " + resultado.get("velocidadPromedio").toString(), font));
				document.add(
						new Paragraph("Intensidad Promedio: " + resultado.get("intensidadPromedio").toString(), font));

			}
			document.close();

			try {
				// Propiedades de la conexión

				String to = "cuentatest111@gmail.com";
				String from = "cuentatest111@gmail.com";
				String pass = "cuentatest11123";
				Properties props = new Properties();
				props.setProperty("mail.smtp.host", "smtp.gmail.com");
				props.setProperty("mail.smtp.starttls.enable", "true");
				props.setProperty("mail.smtp.port", "587");
				props.setProperty("mail.smtp.user", from);
				props.setProperty("mail.smtp.auth", "true");

				// Preparamos la sesion
				Session session = Session.getDefaultInstance(props);

				BodyPart texto = new MimeBodyPart();
				texto.setText("Texto del mensaje");

				// Se compone el adjunto con la imagen
				BodyPart adjunto = new MimeBodyPart();
				adjunto.setDataHandler(new DataHandler(new FileDataSource("iTextHelloWorld.pdf")));
				adjunto.setFileName("archivo.pdf");

				// Una MultiParte para agrupar texto e imagen.
				MimeMultipart multiParte = new MimeMultipart();
				multiParte.addBodyPart(texto);
				multiParte.addBodyPart(adjunto);

				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				message.setSubject("Hola");
				message.setContent(multiParte);

				// Lo enviamos.
				Transport t = session.getTransport("smtp");
				t.connect(from, pass);
				t.sendMessage(message, message.getAllRecipients());

				// Cierre.
				t.close();
				archivo.delete();
				return HttpStatus.OK;

			} catch (Exception e) {
				archivo.delete();
				e.printStackTrace();
				return HttpStatus.BAD_GATEWAY;
			}

		} else {
			// Estadistica de un lugar en especifico
			señales = signalRepository.findAllByLugar(lugar);

			Double velocidadSumatoria = 0.0;
			Double intensidadSumatoria = 0.0;
			Double contador = 0.0;

			for (Signal señal : señales) {
				if (señal.getEstado() != null) {
					velocidadSumatoria = velocidadSumatoria + Double.parseDouble(señal.getVelocidad());
					intensidadSumatoria = intensidadSumatoria + Double.parseDouble(señal.getIntensidad());
					contador++;
				}
			}
			Double velocidadPromedio = velocidadSumatoria / contador;
			Double intensidadPromedio = intensidadSumatoria / contador;

			HashMap<String, Double> resultado = new HashMap<>();
			resultado.put("velocidadPromedio", velocidadPromedio);
			resultado.put("intensidadPromedio", intensidadPromedio);

			File archivo = new File("iTextHelloWorld.pdf");
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(archivo));

			document.open();
			Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);

			document.add(new Paragraph("Lugar: " + lugar, font));
			document.add(new Paragraph("Velocidad Promedio: " + resultado.get("velocidadPromedio").toString(), font));

			document.add(new Paragraph("Intensidad Promedio: " + resultado.get("intensidadPromedio").toString(), font));

			document.close();

			try {
				// Propiedades de la conexión

				String to = "cuentatest111@gmail.com";
				String from = "cuentatest111@gmail.com";
				String pass = "cuentatest11123";
				Properties props = new Properties();
				props.setProperty("mail.smtp.host", "smtp.gmail.com");
				props.setProperty("mail.smtp.starttls.enable", "true");
				props.setProperty("mail.smtp.port", "587");
				props.setProperty("mail.smtp.user", from);
				props.setProperty("mail.smtp.auth", "true");

				// Preparamos la sesion
				Session session = Session.getDefaultInstance(props);

				BodyPart texto = new MimeBodyPart();
				texto.setText("Texto del mensaje");

				// Se compone el adjunto con la imagen
				BodyPart adjunto = new MimeBodyPart();
				adjunto.setDataHandler(new DataHandler(new FileDataSource("iTextHelloWorld.pdf")));
				adjunto.setFileName("archivo.pdf");

				// Una MultiParte para agrupar texto e imagen.
				MimeMultipart multiParte = new MimeMultipart();
				multiParte.addBodyPart(texto);
				multiParte.addBodyPart(adjunto);

				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				message.setSubject("Hola");
				message.setContent(multiParte);

				// Lo enviamos.
				Transport t = session.getTransport("smtp");
				t.connect(from, pass);
				t.sendMessage(message, message.getAllRecipients());

				// Cierre.
				t.close();
				archivo.delete();
				return HttpStatus.OK;

			} catch (Exception e) {
				archivo.delete();
				e.printStackTrace();
				return HttpStatus.BAD_GATEWAY;
			}
		}
	}

}

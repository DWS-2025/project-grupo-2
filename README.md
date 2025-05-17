<h1 align="center">Hola 👋, Somos UPCJ</br><i>(Universidad Príncipe Carlos Juan)</i></h1>
<h3 align="center">Grupo 2 de Desarrollo Web Seguro y fundadores del Aula Visual</h3>


<div align="center">
  <img src="https://github.com/DWS-2025/project-grupo-2/blob/main/content/images/logoAulaVisual.svg" alt="AulaVisual"/>
</div>  

<h3 align="center">Lenguajes && Herramientas</h3>
<p align="center"> 
  <a href="https://www.w3schools.com/css/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/css3/css3-original-wordmark.svg" alt="css3" width="40" height="40"/> </a> 
  <a href="https://git-scm.com/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/git-scm/git-scm-icon.svg" alt="git" width="40" height="40"/> </a> 
  <a href="https://www.w3.org/html/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/html5/html5-original-wordmark.svg" alt="html5" width="40" height="40"/> </a> 
  <a href="https://www.java.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" width="40" height="40"/> </a> 
</p>

<h3 align="center">Entidades</h3>
<ul>
  <li><strong>User</strong>: Representa a los usuarios del sistema (administradores, profesores y estudiantes).</li>
  <li><strong>Course</strong>: Representa los cursos disponibles en la plataforma.</li>
  <li><strong>Submission</strong>: Representa los envíos realizados por los estudiantes en los cursos.</li>
  <li><strong>Module</strong>: Representa los módulos de contenido dentro de un curso.</li>
</ul>

<h3 align="center">Relaciones</h3>
<ul>
  <li>Un <strong>User</strong> puede ser profesor de un <strong>Course</strong> (<code>@OneToOne</code>).</li>
  <li>Un <strong>User</strong> puede estar inscrito en múltiples <strong>Course</strong> como estudiante (<code>@ManyToMany</code>).</li>
  <li>Un <strong>Course</strong> puede tener múltiples <strong>User</strong> como estudiantes (<code>@ManyToMany</code>).</li>
  <li>Un <strong>User</strong> puede tener múltiples <strong>Submission</strong> (<code>@OneToMany</code>).</li>
  <li>Un <strong>Submission</strong> pertenece a un <strong>User</strong> y a un <strong>Course</strong> (<code>@ManyToOne</code>).</li>
  <li>Un <strong>Course</strong> puede tener múltiples <strong>Module</strong> (<code>@OneToMany</code>).</li>
  <li>Un <strong>Module</strong> pertenece a un <strong>Course</strong> (<code>@ManyToOne</code>).</li>
</ul>

<h3 align="center">Imágenes Asociadas</h3>
<ul>
  <li><strong>User</strong>: Imagen de perfil (<code>image</code>, <code>imageFile</code>).</li>
  <li><strong>Course</strong>: Imagen representativa del curso (<code>imageCourse</code>).</li>
</ul>

<h3 align="center">🎯 Roles y Permisos</h3>

#### 👑 Administrador (`role = 0`)
**Permisos:**
- Crear, actualizar y eliminar usuarios.
- Asignar roles a los usuarios.
- Gestionar cursos (crear, actualizar, eliminar).
- Asignar profesores a los cursos.

#### 👨‍🏫 Profesor (`role = 1`)
**Permisos:**
- Ver contenido del curso que enseña.
- Evaluar y gestionar entregas de los estudiantes.

#### 👨‍🎓 Estudiante (`role = 2`)
**Permisos:**
- Ver contenido de los cursos en los que está inscrito.
- Subir entregas para los cursos.
- Ver sus propias entregas y calificaciones.

<h4 align="center">El usuario es dueño de una submission<br/>
El curso es dueño de varios módulos<br/>
El usuario es dueño de su destino, así como el fuego lo es de su llama: nace en lo profundo, y arde por su propia voluntad. 🔥🧙‍♂️

</h4>

<h3 align="center">Diagrama de Entidades</h3>
<div align="center">
  <img src="https://github.com/DWS-2025/project-grupo-2/blob/main/content/diagrams/Database_Diagram.svg" alt="Diagrama de Base de Datos"/>
</div>
<h1 align="center">V2.0</h1>
<h1>TOP 4+1 COMMITS by Mineja2017</h1>
  <p>En el reino del código, donde cada línea es un verso de una antigua saga, te presentamos el top 2+3 de nuestros commits legendarios:</p>

  <h2>1. La edad de la API</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/9537df222fd5989173f697be5d7166ab903db266#diff-f5bcc571ffc6d7e94042ed98e6d3bd30e3e82e550a31b607ee4fee5829aab980">9537df2</a></p>
  <p><strong>Mensaje:</strong> Diversas correciones.</p>
  <p><strong>Descripción:</strong> En una batalla épica contra el error, se sustituyó el uso de <code>==</code> por el método mágico <code>.equals()</code>. Un ajuste sutil pero decisivo, que aseguró que la lógica de eliminación de entregas opere con la precisión de la espada de Glamdring. Marca también el inicio de la edad de la API, estando su majestuosa obra terminada.</p>

  <h2>2. El Ritual de la Base de Datos</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/b3ea196586f7ae0b8923ee5c532e4e55e9d7c94f">b3ea196</a></p>
  <p><strong>Mensaje:</strong> Functional Web apliation with DDBB</p>
  <p><strong>Descripción:</strong> Un hechizo de integración, donde la aplicación web se unificó con su base de datos. Como una alianza de las razas libres de la Tierra Media, se logró una sinergia perfecta entre el frontend y el backend.</p>

  <h2>3. El Conjuro AJAX</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/6f43b74b702b7ad4042f506afabc0c057d767bb9">6f43b74</a></p>
  <p><strong>Mensaje:</strong> AJAX pageable && filter</p>
  <p><strong>Descripción:</strong> Con la agilidad de un elfo y la precisión de un mago, este commit introdujo paginación y filtrado vía AJAX, permitiendo que los datos fluyan tan suaves como el río Anduin.</p>

  <h2>4. La Intervención Divina</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/cfe0e37dfe3751076ef951a45c00be537778b203">cfe0e37</a></p>
  <p><strong>Mensaje:</strong> Divine Intervention – Compile and works (Most of it)</p>
  <p><strong>Descripción:</strong> Una auténtica intervención celestial. En este commit, los dioses del desarrollo se manifestaron y otorgaron la bendición de compilar sin errores, transformando el caos en armonía.</p>

  <h2>3,5 + 1,5. La Gloriosa Documentación</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/afd6293dbf3e9724b8c00e87530d7e174dc9d558">afd6293</a></p>
  <p><strong>Mensaje:</strong> Created documentation</p>
  <p><strong>Descripción:</strong> Del coro de Ilúvatar y los Ainur brotan Eä (El universo) y esta documentación que ayudará al futuro concilio blanco (developers) en el uso de la API previamente implementada.</p>

  <h1>TOP 3+2 COMMITS by Granusti44</h1>

  <h2>1. La Última Vigilia del Granusti</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/1adbeca4af5137a1ab7ce8d7c2250a28628162b2">1adbeca</a></p>
  <p><strong>Mensaje:</strong> Oops</p>
  <p><strong>Descripción:</strong> En las sombrías horas de la madrugada, el gran mago (Granusti44) libró su batalla final contra el Balrog (Mineja2017), antes de la hora del destino final (La clase del día siguiente).</p>

  <h2>2. La Alianza del Mapper de User</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/5a6572f7a087b14d4d1f2fffa1712bd74279ac46">5a6572f</a></p>
  <p><strong>Mensaje:</strong> Finiching compiling errors</p>
  <p><strong>Descripción:</strong> Diversas clases fueron tocadas por la magia del UserMapper, permitiendo que la transformación a DTOs fluyera con precisión élfica.</p>

  <h2>3. El Despertar de la API</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/a8ae879f88e0253013b2da258453d43426e4c27a">a8ae879</a></p>
  <p><strong>Mensaje:</strong> Api implementation finished</p>
  <p><strong>Descripción:</strong> El último endpoint fue sellado, marcando el fin de una era. La API REST despertó como un dragón sobre los campos de datos, lista para servir al reino con sabiduría y estructura.</p>

  <h2>4. El Legado del Módulo</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/9de45f7d9e3aa9f398c99997778497308e6e4e99">9de45f7</a></p>
  <p><strong>Mensaje:</strong> Compiling errors</p>
  <p><strong>Descripción:</strong> ModuleMapper, el vínculo sagrado entre la lógica del dominio y su proyección en el reino de los datos, fue forjado con la precisión de un enano artesano.</p>

  <h2>2*2 + 1. El Cierre del Grimorio</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/46a44414f2aee4d5eba99a43239ffc480c53be58">46a4441</a></p>
  <p><strong>Mensaje:</strong> Update README.md</p>
  <p><strong>Descripción:</strong> Este commit marca el fin de la escritura del códice. No fue un gran cambio en el código, sino un cambio en el alma del proyecto. La espada ha sido envainada; ahora, se canta la historia.</p>

  <p><em>Y así esta edad termina dejando paso a la siguiente, en la que, con mucha seguridad, viviremos muchas más aventuras épicas protegiendo lo que hemos construido frente a las temibles bestias de Morgoth.</em></p>
<h1 align="center">V3.0</h1>
<h1>TOP 6-1 COMMITS by Mineja2017</h1>
<h2>1. User Service Add Security</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/f1617f5d3ab5429f8c9316fbdae5d40652ffa33a">9537df2</a></p>
  <p><strong>Mensaje:</strong> User service add security finished</p>
  <p><strong>Descripción:</strong> En este conjuro, el servicio de usuarios recibe capas impenetrables de seguridad: autenticación reforzada, control de accesos y encriptación de datos sensibles, sellando las puertas de Mordor a los intrusos.</p>
  
  <h2>2. REST Module Security</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/b8f0c73bc56dff740a40861413ee99b34b2cd70f">9537df2</a></p>
  <p><strong>Mensaje:</strong> REST-module-security finished</p>
  <p><strong>Descripción:</strong> Con la elegancia de un guardián élfico, este commit endurece los endpoints REST de módulos, envolviéndolos en runas de autorización y respondiendo con sabiduría divina a cada petición.</p>

  <h2>3. Finished REST User Security</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/631ddd84f1425df00f804735f844fe31a584db07">9537df2</a></p>
  <p><strong>Mensaje:</strong> Finished REST user security</p>
  <p><strong>Descripción:</strong> Tras una larga peregrinación por rutas de código, la seguridad REST para usuarios alcanza su culminación: roles, permisos y validaciones se alinean, como las estrellas en la Puerta Negra.</p>

  <h2>4. Added Sanitization?</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/0c4edbbef62a7b2a72539b606e0d3a52c6217beb">9537df2</a></p>
  <p><strong>Mensaje:</strong> added sanitization? not quite sure</p>
  <p><strong>Descripción:</strong> Como Gandalf plantando su bastón en el Puente de Khazad-dûm, este commit invocó el OWASP HTML Sanitizer para detener el avance de cualquier inyección maligna; tal como el Mago gritó “¡No pasarás!” y partió el puente bajo el Balrog, nuestra sanitización fractura y arroja al abismo cualquier script malicioso que intente subyugar el sistema.</p>
  <img src="https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExNHN0NWl4bDcya3o1aDV3eW9yYmZsbjY1NjcweWlwMnhwc3NyOW9nYiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/njYrp176NQsHS/giphy.gif">

  <h2>"10/2". Securely Save Submission in Disk</h2>
  <p><strong>Commit:</strong> <a href="https://github.com/DWS-2025/project-grupo-2/commit/c18d96676401cd295bc997d00f7749679b2ea813">9537df2</a></p>
  <p><strong>Mensaje:</strong> Securely save submission in disk</p>
  <p><strong>Descripción:</strong> Con la bendición de Ilúvatar y el resplandor de las Valar, las entregas ya no duermen en frágiles blobs en las entrañas de la montaña solitaria (La base de Datos), pues ahora reposan seguras en el disco, bajo custodias de rutas cifradas y control de integridad.</p>
  
<h3 align="center">Miembros</h3>
<div align="center">
  👨‍🎓 Jaime García González aka <a href="https://github.com/Mineja2017">Mineja2017</a>, j.garciago.2023@alumnos.urjc.es<br/>
  👨‍🎓 Victor Ruiz Muñoz aka <a href="https://github.com/Gransuti44">Granusti44</a>, v.ruiz.2022@alumnos.urjc.es
</div>

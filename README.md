<h1 align="center">Hola 👋, Somos UPCJ</br><i>(Universidad Príncipe Carlos Juan)</i></h1>
<h3 align="center">Grupo 2 de Desarrollo Web Seguro y fundadores del Aula Visual</h3>


<div align="center">
  <img src="https://github.com/DWS-2025/project-grupo-2/blob/main/AulaVisual/src/main/resources/static/images/1.svg" alt="AulaVisual"/>
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

<h3 align="center">Miembros</h3>
<div align="center">
  👨‍🎓 Jaime García González aka Mineja2017, j.garciago.2023@alumnos.urjc.es<br/>
  👨‍🎓 Victor Ruiz Muñoz aka Granusti44, v.ruiz.2022@alumnos.urjc.es
</div>

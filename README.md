abrt-log-jboss
=================

ABRT error handler for JBoss AS 7

Compile the jar file with maven: `mvn package`

Unzip the archive created in the `target/zip` folder at `$JBOSS_HOME/modules`.

Modify the JBoss configuration in `standalone/configuration/standalone.xml` like this:

<pre>
&lt;subsystem xmlns="urn:jboss:domain:logging:1.1"&gt;
    ...
    &lt;custom-handler name="ABRT" class="org.abrt.log.jboss.AbrtLogHandler" module="org.abrt.log&gt;
        &lt;level name="ERROR"/&gt;
    &lt;/custom-handler&gt;
    ...
    &lt;root-logger&gt;
        &lt;level name="INFO"/&gt;
        &lt;handlers&gt;
            &lt;handler name="CONSOLE"/&gt;
            &lt;handler name="FILE"/&gt;
            &lt;handler name="ABRT"/&gt;
        &lt;/handlers&gt;
    &lt;/root-logger&gt;
&lt;/subsystem&gt;
</pre>


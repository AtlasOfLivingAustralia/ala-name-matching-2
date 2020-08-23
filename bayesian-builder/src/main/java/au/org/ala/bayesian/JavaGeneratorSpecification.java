package au.org.ala.bayesian;

import au.org.ala.util.SimpleIdentifierConverter;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * How to generate a specific class for a java implementation of one of the network components.
 * <p>
 * Implemented classes are designed to allow a suitable superclass to hold common
 * fucntionality and an implementation class name to hold specific hand-rolled code for the
 * class.
 * </p>
 * <p>
 * Each specification uses an interface to act as the basis for the implemented class.
 * The interface may be parameterised and
 * </p>
 *
 * @param <I> The interface this class
 */
@Slf4j
public class JavaGeneratorSpecification<I> {
    /** The function of the class. Used to make appropriate names */
    @Getter
    private final String function;
    /** The template name. If null then this class is not generated and only exists if there is an implementation class. */
    @Getter
    private final String template;
    /** The full interface class this implements */
    @Getter
    private final Class<I> interfaceClass;
    /** The parameters used to specialise the interface/other classes */
    @Getter
    private final Map<Class<?>, JavaGeneratorSpecification> parameters;
    /** Actually generate this class? */
    @Getter
    @Setter
    private boolean generate;
    /** The full name of the superclass */
    @Getter
    @Setter
    private String superClassName;
    /** The full, parameterised name of the implementation class */
    @Getter
    @Setter
    private String implementationClassName;

    /**
     * Create a specification.
     *
     * @param function The name of the function this class performs
     * @param template The name of the template that this class generates. Null for a non-generating placeholder spec.
     * @param interfaceClass The interface that the generated class implements
     * @param parameters Any other classes that this class depends upon.
     */
    public JavaGeneratorSpecification(@NonNull String function, String template, @NonNull Class<I> interfaceClass, JavaGeneratorSpecification<?>... parameters) {
        this.function = function.toLowerCase();
        this.template = template;
        this.interfaceClass = interfaceClass;
        this.generate = true;
        this.parameters = Arrays.stream(parameters).collect(Collectors.toMap(p -> p.getInterfaceClass(), p -> p));
    }

    /**
     * Add a parameter (needed for circularities)
     *
     * @param spec The parameter
     * @param <I> The inferace type
     */
    public <I> void addParameter(JavaGeneratorSpecification<I> spec) {
        this.parameters.put(spec.getInterfaceClass(), spec);
    }

    /**
     * A specification for a specific compiler output.
     *
     * @param compiler The compiler
     * @param packageName The base package name
     *
     * @return A contextualised spec.
     */
    public Context withContext(NetworkCompiler compiler, String packageName) {
        return new Context(compiler, packageName);
    }

    /**
     * The generator specification wrapped with a specific compiler.
     */
    @Value
    public class Context {
        private final NetworkCompiler compiler;
        private final String packageName;
        private final String baseClassName;

        public Context(NetworkCompiler compiler, String packageName) {
            this.compiler = compiler;
            this.packageName = packageName;
            this.baseClassName = SimpleIdentifierConverter.JAVA_CLASS.convert(this.compiler.getNetwork());
        }

        /**
         * Get the class name to use.
         * <p>
         * Generally, this is the name constructed from the network name and the function.
         * However, if there is no template, meaning that a class is not generated, the interface name is used.
         * </p>
         *
         * @return The class name
         */
        public String getClassName() {
            if (JavaGeneratorSpecification.this.template == null)
                return JavaGeneratorSpecification.this.interfaceClass.getSimpleName();
            return this.baseClassName + StringUtils.capitalize(JavaGeneratorSpecification.this.function);
        }

        /**
         * Get the fully qualified class name.
         *
         * @see #getClassName()
         *
         * @return The fully qualified (with package) class name
         */
        public String getFullClassName() {
            if (JavaGeneratorSpecification.this.template == null)
                return JavaGeneratorSpecification.this.interfaceClass.getName();
            return this.packageName + "." + this.baseClassName + StringUtils.capitalize(JavaGeneratorSpecification.this.function);
        }

        /**
         * Get any superclass name.
         *
         * @return The name of any superclass that the generated subclass extends.
         */
        public String getSuperClassName() {
            return JavaGeneratorSpecification.this.superClassName;
        }

        /**
         * Get any implementation class.
         * <p>
         * If there is a specific implementation class, return that.
         * If there is no template (generated class) then return null.
         * Otherwise return the name of the generated class.
         * </p>
         *
         * @return The implementation class
         */
        public String getImplementationClassName() {
            if (JavaGeneratorSpecification.this.implementationClassName != null)
                return JavaGeneratorSpecification.this.implementationClassName;
            if (JavaGeneratorSpecification.this.template == null)
                return null;
            return this.getClassName();
        }

        /**
         * Get the implementation class with full package name.
         *
         * @return The full implementation class name
         */
        public String getFullImplementationClassName() {
            return JavaGeneratorSpecification.this.implementationClassName != null ? JavaGeneratorSpecification.this.implementationClassName : this.getFullClassName();
        }

        /**
         * Get the name of the template variable that holds the class name
         *
         * @return The class variable
         */
        public String getClassVariable() {
            return JavaGeneratorSpecification.this.function + "ClassName";
        }

        /**
         * Get the name of the template variable that holds the super class name
         *
         * @return The superclass variable
         */
        public String getSuperClassVariable() {
            return JavaGeneratorSpecification.this.function + "SuperClassName";
        }


        /**
         * Get the name of the template variable that holds the implementation class name
         *
         * @return The implementation class variable
         */
        public String getImplementationClassVariable() {
            return JavaGeneratorSpecification.this.function + "ImplementationClassName";
        }

        public void populate(Environment environment, BeansWrapper wrapper, Set<String> imports) {
            if (this.getSuperClassName() != null) {
                log.debug("Adding variable {}={}", "superClassName", this.getSuperClassName());
                environment.setVariable("superClassName", new StringModel(this.getSuperClassName(), wrapper));
                this.addImport(this.getSuperClassName(), imports);
            }
            log.debug("Adding variable {}={}", "className", this.getClassName());
            environment.setVariable("className", new StringModel(this.getClassName(), wrapper));
            log.debug("Adding variable {}={}", "implementationClassName", this.getImplementationClassName());
            environment.setVariable("implementationClassName", new StringModel(this.getImplementationClassName(), wrapper));
            for (JavaGeneratorSpecification ps: JavaGeneratorSpecification.this.parameters.values()) {
                Context c = ps.withContext(this.compiler, this.packageName);
                log.debug("Adding variable {}={}", c.getClassVariable(), c.getClassName());
                environment.setVariable(c.getClassVariable(), new StringModel(c.getClassName(), wrapper));
                if (c.getImplementationClassName() != null) {
                    log.debug("Adding variable {}={}", c.getImplementationClassVariable(), c.getImplementationClassName());
                    environment.setVariable(c.getImplementationClassVariable(), new StringModel(c.getImplementationClassName(), wrapper));
                }
                if (c.getFullImplementationClassName() != null)
                    this.addImport(c.getFullImplementationClassName(), imports);
                this.addImport(c.getFullClassName(), imports);
            }
        }

        /**
         * Conditionally add a class to the import set.
         * <p>
         * An import is added of it is not part of the supplied package.
         * </p>
         *
         * @param className The full class name
         * @param imports The current set of imports
         */
        public void addImport(String className, Set<String> imports) {
            if (className.startsWith("java.lang.") && className.indexOf('.', 10) < 0)
                return;
            if (className.startsWith(this.packageName) && className.indexOf('.', this.packageName.length() + 1) < 0)
                return;
            imports.add(className);
        }
    }
}

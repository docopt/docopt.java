``docopt.java`` is a Java port of docopt
======================================================================

Isn't it awesome how ``Apache Commons CLI`` and the dozens of other Java command
line parsers generate help messages based on your code?!

*Hell no!*  You know what's awesome?  It's when the option parser *is*
generated based on the beautiful help message that you write yourself!
This way you don't need to write this stupid repeatable parser-code,
and instead can write only the help message--*the way you want it*.

**docopt.java** helps you create most beautiful command-line interfaces
*easily*:

.. code:: java

  import java.util.Map;
      
  import org.docopt.Docopt;
      
  public final class NavalFate {
      
  private static final String doc =
      "Naval Fate.\n"
      + "\n"
      + "Usage:\n"
      + "  naval_fate ship new <name>...\n"
      + "  naval_fate ship <name> move <x> <y> [--speed=<kn>]\n"
      + "  naval_fate ship shoot <x> <y>\n"
      + "  naval_fate mine (set|remove) <x> <y> [--moored | --drifting]\n"
      + "  naval_fate (-h | --help)\n"
      + "  naval_fate --version\n"
      + "\n"
      + "Options:\n"
      + "  -h --help     Show this screen.\n"
      + "  --version     Show version.\n"
      + "  --speed=<kn>  Speed in knots [default: 10].\n"
      + "  --moored      Moored (anchored) mine.\n"
      + "  --drifting    Drifting mine.\n"
      + "\n";
  
      public static void main(String[] args) {
        Map<String, Object> opts =
          new Docopt(doc).withVersion("Naval Fate 2.0").parse(args);
        System.out.println(opts);
      }
  }

Differences from the reference implementation
======================================================================

- Because Java does not support optional or named arguments, this port uses the
  Builder pattern to configure the parser instead of a simple method call.

- Because Java does not provide a way (native) way to read a class's Javadoc,
  there is no idiomatic way to supply the ``doc`` or ``version`` arguments.
  This implementation provides convenience methods to read these values from
  streams (e.g. files within the JAR) in addition to accepting String arguments.

- Because Java does not provide a way to get command line arguments other than
  in a ``main`` method, the ``argv`` parameter is required.
  
- Exiting the application when parsing arguments has been made optional. See the
  ``withExit`` method.  

Installation
======================================================================

You can build a JAR using `Maven http://maven.apache.org/` and include it as a
dependency in your project. **docopt.java** is not currently available from Maven
central.

Alternatively, you can just copy the ``org.docopt`` package into your project--it
is self-contained.

Or you can use https://jitpack.io/ as a package repository:

Add the JitPack repository to your build file

.. code:: xml

  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>

Add the latest snapshot of docopt to your dependencies

.. code:: xml

  <dependency>
    <groupId>com.github.docopt</groupId>
    <artifactId>docopt.java</artifactId>
    <version>-SNAPSHOT</version>
  </dependency>

**docopt.java** is tested with Java 6 and Java 7.

API
======================================================================

.. code:: java

  import org.docopt.Docopt;
  
.. code:: java

  public Docopt(String doc)

``Docopt`` takes one required argument:

- ``doc`` is a ``String`` that contains a **help message** that will be parsed to
  create the option parser.  The simple rules of how to write such a
  help message are given in next sections.  Here is a quick example of
  such a string:

.. code:: java

  static final String doc =
      "Usage: my_program [-hso FILE] [--quiet | --verbose] [INPUT ...]\n"
      + "\n"
      + "-h --help    show this\n"
      + "-s --sorted  sorted output\n"
      + "-o FILE      specify output file [default: ./test.txt]\n"
      + "--quiet      print less text\n"
      + "--verbose    print more text\n"
      + "\n";

.. code:: java

  public Docopt(String doc)
  public Docopt(InputStream doc)
  public Docopt(InputStream doc, Charset charset)

Constructs an option parser from the ``doc`` argument or throws a
``DocoptLanguageError`` if it is malformed. If ``doc`` is an ``InputStream``,
the stream is read using the specified ``CharSet`` (``UTF-8`` by default).

.. code:: java

  public Map<String, Object> parse(List<String> argv)
  public Map<String, Object> parse(String... argv)
  
``parse`` takes one required argument:

- ``argv`` is an argument vector. The vector may be given as a ``List`` or as an
  array of ``Strings``. *Note that calling this method with no argument is
  equivalent to a giving an empty array!*

The **return** value is a ``Map`` with options, arguments, and commands as keys,
spelled exactly like in your help message. Long versions of options are given
priority. For example, if you invoke the top example as::

  naval_fate.py ship Guardian move 100 150 --speed=15

the return ``Map`` will be:

.. code:: java

  {--version=false,     remove=false,
   --speed=15,          ship=true,
   <name>=[Guardian],   set=false,
   <y>=150,             <x>=100,
   --moored=false,      new=false,
   --drifting=false,    shoot=false,
   mine=false,          --help=false,
   move=true}

.. code:: java

  public Docopt withHelp(boolean help)

``withHelp`` takes one required argument:

- ``help``, by default ``true``, specifies whether the parser should
  automatically print the help message (supplied as ``doc``) and
  terminate, in case ``-h`` or ``--help`` option is encountered
  (options should exist in usage pattern, more on that below). If you
  want to handle ``-h`` or ``--help`` options manually (as other
  options), invoke ``withHelp(false)``.

    Note, when ``docopt`` is set to automatically handle the ``-h`` and
    ``--help`` options, you still need to mention them in usage pattern for this
    to work. Also, for your users to know about them.

.. code:: java

  public Docopt withVersion(String version)

- ``version``, by default ``null``, specifies the version of your program. If
  supplied, then, (assuming ``--version`` option is mentioned in usage pattern)
  when parser encounters the ``--version`` option, it will print the supplied
  version and terminate.

    Note, when ``docopt`` is set to automatically handle the ``--version``
    option, you still need to mention it in usage pattern for this to work.
    Also, for your users to know about them.

.. code:: java

  public Docopt withOptionsFirst(boolean optionsFirst)

- ``optionsFirst``, by default ``false``.  If set to ``true`` will
  disallow mixing options and positional argument. I.e. after first
  positional argument, all arguments will be interpreted as positional
  even if the look like options. This can be used for strict
  compatibility with POSIX, or if you want to dispatch your arguments
  to other programs.

.. code:: java

  public Docopt withExit(boolean exit)

- ``exit``, by default ``true``. If set to ``false`` will cause ``parse`` to
  throw a ``DocoptExit`` exception instead of terminating the application.

Help message format
======================================================================

Help message consists of 2 parts:

- Usage pattern, e.g.::

    Usage: my_program [-hso FILE] [--quiet | --verbose] [INPUT ...]

- Option descriptions, e.g.::

    -h --help    show this
    -s --sorted  sorted output
    -o FILE      specify output file [default: ./test.txt]
    --quiet      print less text
    --verbose    print more text

Their format is described below; other text is ignored.

Usage pattern format
----------------------------------------------------------------------

**Usage pattern** is a substring of ``doc`` that starts with
``usage:`` (case *insensitive*) and ends with a *visibly* empty line.
Minimum example:

.. code:: java

    static final String USAGE = "Usage: my_program";

The first word after ``usage:`` is interpreted as your program's name.
You can specify your program's name several times to signify several
exclusive patterns:

.. code:: java

  static final String USAGE = 
      "Usage: my_program FILE\n" +
      "       my_program COUNT FILE";

Each pattern can consist of the following elements:

- **<arguments>**, **ARGUMENTS**. Arguments are specified as either
  upper-case words, e.g. ``my_program CONTENT-PATH`` or words
  surrounded by angular brackets: ``my_program <content-path>``.

- **--options**.  Options are words started with dash (``-``), e.g.
  ``--output``, ``-o``.  You can "stack" several of one-letter
  options, e.g. ``-oiv`` which will be the same as ``-o -i -v``. The
  options can have arguments, e.g.  ``--input=FILE`` or ``-i FILE`` or
  even ``-iFILE``. However it is important that you specify option
  descriptions if you want your option to have an argument, a default
  value, or specify synonymous short/long versions of the option (see
  next section on option descriptions).

- **commands** are words that do *not* follow the described above
  conventions of ``--options`` or ``<arguments>`` or ``ARGUMENTS``,
  plus two special commands: dash "``-``" and double dash "``--``"
  (see below).

Use the following constructs to specify patterns:

- **[ ]** (brackets) **optional** elements.  e.g.: ``my_program
  [-hvqo FILE]``

- **( )** (parens) **required** elements.  All elements that are *not*
  put in **[ ]** are also required, e.g.: ``my_program
  --path=<path> <file>...`` is the same as ``my_program
  (--path=<path> <file>...)``.  (Note, "required options" might be not
  a good idea for your users).

- **|** (pipe) **mutually exclusive** elements. Group them using **(
  )** if one of the mutually exclusive elements is required:
  ``my_program (--clockwise | --counter-clockwise) TIME``. Group
  them using **[ ]** if none of the mutually-exclusive elements are
  required: ``my_program [--left | --right]``.

- **...** (ellipsis) **one or more** elements. To specify that
  arbitrary number of repeating elements could be accepted, use
  ellipsis (``...``), e.g.  ``my_program FILE ...`` means one or
  more ``FILE``-s are accepted.  If you want to accept zero or more
  elements, use brackets, e.g.: ``my_program [FILE ...]``. Ellipsis
  works as a unary operator on the expression to the left.

- **[options]** (case sensitive) shortcut for any options.  You can
  use it if you want to specify that the usage pattern could be
  provided with any options defined below in the option-descriptions
  and do not want to enumerate them all in usage-pattern.

- "``[--]``". Double dash "``--``" is used by convention to separate
  positional arguments that can be mistaken for options. In order to
  support this convention add "``[--]``" to your usage patterns.

- "``[-]``". Single dash "``-``" is used by convention to signify that
  ``stdin`` is used instead of a file. To support this add "``[-]``"
  to your usage patterns. "``-``" acts as a normal command.

If your pattern allows to match argument-less option (a flag) several
times::

  Usage: my_program [-v | -vv | -vvv]

then number of occurrences of the option will be counted. I.e.
``args['-v']`` will be ``2`` if program was invoked as ``my_program
-vv``. Same works for commands.

If your usage patterns allows to match same-named option with argument
or positional argument several times, the matched arguments will be
collected into a list::

  Usage: my_program <file> <file> --path=<path>...

I.e. invoked with ``my_program file1 file2 --path=./here
--path=./there`` the returned dict will contain ``args['<file>'] ==
['file1', 'file2']`` and ``args['--path'] == ['./here', './there']``.


Option descriptions format
----------------------------------------------------------------------

**Option descriptions** consist of a list of options that you put
below your usage patterns.

It is necessary to list option descriptions in order to specify:

- synonymous short and long options,
- if an option has an argument,
- if option's argument has a default value.

The rules are as follows:

- Every line in ``doc`` that starts with ``-`` or ``--`` (not counting
  spaces) is treated as an option description, e.g.::

    Options:
      --verbose   # GOOD
      -o FILE     # GOOD
    Other: --bad  # BAD, line does not start with dash "-"

- To specify that option has an argument, put a word describing that
  argument after space (or equals "``=``" sign) as shown below. Follow
  either <angular-brackets> or UPPER-CASE convention for options'
  arguments.  You can use comma if you want to separate options. In
  the example below, both lines are valid, however you are recommended
  to stick to a single style.::

    -o FILE --output=FILE       # without comma, with "=" sign
    -i <file>, --input <file>   # with comma, without "=" sing

- Use two spaces to separate options with their informal description::

    --verbose More text.   # BAD, will be treated as if verbose option had
                           # an argument "More", so use 2 spaces instead
    -q        Quit.        # GOOD
    -o FILE   Output file. # GOOD
    --stdout  Use stdout.  # GOOD, 2 spaces

- If you want to set a default value for an option with an argument,
  put it into the option-description, in form ``[default:
  <my-default-value>]``::

    --coefficient=K  The K coefficient [default: 2.95]
    --output=FILE    Output file [default: test.txt]
    --directory=DIR  Some directory [default: ./]

- If the option is not repeatable, the value inside ``[default: ...]``
  will be interpreted as string.  If it *is* repeatable, it will be
  splited into a list on whitespace::

    Usage: my_program [--repeatable=<arg> --repeatable=<arg>]
                         [--another-repeatable=<arg>]...
                         [--not-repeatable=<arg>]

    # will be ['./here', './there']
    --repeatable=<arg>          [default: ./here ./there]

    # will be ['./here']
    --another-repeatable=<arg>  [default: ./here]

    # will be './here ./there', because it is not repeatable
    --not-repeatable=<arg>      [default: ./here ./there]

Changelog
======================================================================

**docopt.java** follows `semantic versioning <http://semver.org>`_.

- 0.6.0 Initial port based on version 0.6.1 of the `reference implementation
  <https://github.com/docopt/docopt>`_. All language agnostic tests pass.

# xml2csv

Simple XML to CSV conversion utility.

## What it does exactly?

It converts XML files to CSV if the XML file is in following format:

    <root>
        <item>
            <value1>...</value1>
            <value2>...</value2>
            <value3>...</value3>
        </item>
        <item>
            <value1>...</value1>
            <value3>...</value3>
        </item>
        ...
    </root>

&lt;root&gt; and &lt;item&gt; elements can use any name, these names are ignored. &lt;value1&gt;
and other value elements are important as these are converted to the CSV file.

## Prerequisities

* JRE or JDK 7+
* Apache Maven 3+ (just in case you want to compile the application yourself)

## Compilation

You are not required to compile the application yourself, you can download
latest binary from https://github.com/fordfrog/xml2csv/downloads. Anyway,
compilation of ruian2pgsql is easy. Once you install Apache Maven, you just need
to run `mvn package` in the root directory of the sources, where pom.xml file is
located.

## Running

Here is the usage information that xml2csv outputs if run without parameters:

    Usage: java -jar xml2csv-*.jar --columns <columns> --input <file> --output <file>

    General command line switches:

    --columns <columns>
        List of columns that should be output to the CSV file. These names must
        correspond to the element names within the item element.
    --input <file>
        Path to the input XML file.
    --output <file>
        Path to the output CSV file. Output file content is always in UTF-8 encoding.

    Filtering rows:

    --filter-column <name>
        Column on which the filter should be applied. When specifying filter command
        line switches, you must use this switch as the first one as it initializes
        new filter. You can specify more filters, each one beginning with this
        switch. You can filter the rows even on columns that are not part of the
        output. Filtering is performed before remapping.
    ..filter.values <file>
        Path to file containing values that the filter should use. Empty rows are
        added to the values too.
    --filter-exclude
        Excludes all rows where the column value matches one of the specified values.
    --filter-include
        Includes all rows where the column value matches one of the specified values.
        This is the default behavior if --filter-exclude|--filter-include is not
        specified.

    Remapping (replacing) values:

    --remap-column <name>
        Column in which original values should be replaced with values from map
        file. When specifying remapping command line switches, you must use this
        switch as the first one as it initializes new remapping. You can specify
        more remappings, each one beginning with this switch. Remapping is performed
        after filtering.
    --remap-map <file>
        Path to file containing original value and new value pairs. The file uses
        CSV format. Values can be escaped either using single-quote (') or
        double-quote ("). Quotes within values can be escaped either doubling them
        ("" and '') or backslash-escaping them (\" and \').

Characters encoding:

    Application expects all files being in UTF-8 encoding.

## To do

Nothing at this moment.

## License

xml2csv is distributed under MIT license.

## Changelog

## Version 1.1.0

* Added support for filtering rows.
* Added support for remapping (replacing) values.

## Version 1.0.0

Initial release.
package eu.keep.gui.wizard.ea.model;

import java.io.File;

public class Emulator {

    public File folder;
    public final String emulator_id, name, version, exec_type, exec_name, description, language_id, package_name,package_type, package_version, _package, user_instructions;

    public Emulator(File folder, String emulator_id, String name, String version, String exec_type, String exec_name,
                    String description, String language_id, String package_name, String package_type,
                    String package_version, String _package, String user_instructions) {
        this.folder = folder;
        this.emulator_id = emulator_id;
        this.name = name;
        this.version = version;
        this.exec_type = exec_type;
        this.exec_name = exec_name;
        this.description = description;
        this.language_id = language_id;
        this.package_name = package_name;
        this.package_type = package_type;
        this.package_version = package_version;
        this._package = _package;
        this.user_instructions = user_instructions;
    }
}

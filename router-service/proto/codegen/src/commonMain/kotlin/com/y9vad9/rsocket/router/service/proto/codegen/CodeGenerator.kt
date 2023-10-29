package com.y9vad9.rsocket.router.service.proto.codegen

import com.squareup.wire.schema.Location
import com.squareup.wire.schema.SchemaLoader
import okio.FileSystem
import okio.Path

public class CodeGenerator(
    private val fileSystem: FileSystem,
) {
    public fun generate(rootPath: Path, outputPath: Path) {
        val files = fileSystem.listRecursively(rootPath)
            .filter { fileSystem.metadata(it).isRegularFile }
            .toList()

        val sourcePath = files.map { path ->
            val metadata = fileSystem.metadata(path)

            Location(rootPath.name, path = path.name)
        }

        val schemaLoader = SchemaLoader(fileSystem)

        schemaLoader.initRoots(sourcePath)

        schemaLoader.loadSchema()
            .protoFiles
            .map { file ->
                ProtoFileTransformer.transform(file)
            }.forEach { file ->
                val path = outputPath.resolve(file.packageName.replace('.', '/'))
                    .toNioPath()

                file.writeTo(path)
            }
    }
}
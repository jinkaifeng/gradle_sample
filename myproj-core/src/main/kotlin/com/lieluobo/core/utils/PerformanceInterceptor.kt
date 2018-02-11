package com.lieluobo.core.utils

import com.baomidou.mybatisplus.toolkit.SqlUtils
import com.baomidou.mybatisplus.toolkit.StringUtils
import org.apache.ibatis.executor.Executor
import org.apache.ibatis.mapping.BoundSql
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.mapping.ParameterMode
import org.apache.ibatis.plugin.*
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.ResultHandler
import org.apache.ibatis.session.RowBounds
import org.slf4j.LoggerFactory
import java.util.*


/**
 *
 *
 * 性能分析拦截器，用于输出每条 SQL 语句及其执行时间
 *
 *
 * @author hubin
 * @Date 2016-07-07
 */
@Intercepts(Signature(type = Executor::class, method = "query", args = arrayOf(MappedStatement::class, Any::class, RowBounds::class, ResultHandler::class)), Signature(type = Executor::class, method = "update", args = arrayOf(MappedStatement::class, Any::class)))
class PerformanceInterceptor : Interceptor {

    /**
     * SQL 执行最大时长，超过自动停止运行，有助于发现问题。
     */
    var maxTime: Long = 0

    var isFormat = false

    @Throws(Throwable::class)
    override fun intercept(invocation: Invocation): Any {
        val mappedStatement = invocation.args[0] as MappedStatement
        var parameterObject: Any? = null
        if (invocation.args.size > 1) {
            parameterObject = invocation.args[1]
        }

        val statementId = mappedStatement.id
        val boundSql = mappedStatement.getBoundSql(parameterObject)
        val configuration = mappedStatement.configuration
        val sql = SqlUtils.sqlFormat(boundSql.sql, isFormat)

        val params = getParams(boundSql, parameterObject, configuration)

        // long start = SystemClock.now();
        val result = invocation.proceed()
        // long end = SystemClock.now();
        // long timing = end - start;
        LOGGER.info("ID：" + statementId + " Execute SQL：" + sql + " SQL Params:" + params.toString())
        // if (maxTime >= 1 && timing > maxTime) {
        // throw new MybatisPlusException(" The SQL execution time is too large, please
        // optimize ! ");
        // }
        return result
    }

    override fun plugin(target: Any): Any {
        return if (target is Executor) {
            Plugin.wrap(target, this)
        } else target
    }

    override fun setProperties(prop: Properties) {
        // TODO
    }

    private fun getParams(boundSql: BoundSql, parameterObject: Any?, configuration: Configuration): List<String> {
        val parameterMappings = boundSql.parameterMappings
        val typeHandlerRegistry = configuration.typeHandlerRegistry
        val params = ArrayList<String>()
        if (parameterMappings != null) {
            for (i in parameterMappings.indices) {
                val parameterMapping = parameterMappings[i]
                if (parameterMapping.mode != ParameterMode.OUT) {
                    val value: Any?
                    val propertyName = parameterMapping.property
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName)
                    } else if (parameterObject == null) {
                        value = null
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.javaClass)) {
                        value = parameterObject
                    } else {
                        val metaObject = configuration.newMetaObject(parameterObject)
                        value = metaObject.getValue(propertyName)
                    }
                    params.add(StringUtils.sqlParam(value))
                }
            }
        }
        return params
    }

    companion object {

        /**
         * 日志
         */
        private val LOGGER = LoggerFactory.getLogger(PerformanceInterceptor::class.java)
    }
}
